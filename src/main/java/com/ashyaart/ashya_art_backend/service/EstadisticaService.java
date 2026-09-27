package com.ashyaart.ashya_art_backend.service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.ashyaart.ashya_art_backend.entity.Curso;
import com.ashyaart.ashya_art_backend.entity.Producto;
import com.ashyaart.ashya_art_backend.entity.TarjetaRegalo;
import com.ashyaart.ashya_art_backend.entity.VisitaDiaria;
import com.ashyaart.ashya_art_backend.model.EstadisticasDto;
import com.ashyaart.ashya_art_backend.model.VisitaDto;
import com.ashyaart.ashya_art_backend.repository.CursoCompraDao;
import com.ashyaart.ashya_art_backend.repository.CursoDao;
import com.ashyaart.ashya_art_backend.repository.ProductoDao;
import com.ashyaart.ashya_art_backend.repository.TarjetaRegaloDao;
import com.ashyaart.ashya_art_backend.repository.VisitaDiariaDao;
import com.ashyaart.ashya_art_backend.util.ClasificadorVisita;

/** Registro de visitas de la web pública y resumen para la sección Statistics del panel. */
@Service
public class EstadisticaService {

    private static final Logger logger = LoggerFactory.getLogger(EstadisticaService.class);
    private static final ZoneId ZONA = ZoneId.of("Europe/Berlin");
    private static final Pattern FICHA = Pattern.compile("^/(workshops|gift-cards|products)/(\\d+)(-.*)?$");
    private static final DateTimeFormatter DIA = DateTimeFormatter.ofPattern("d MMM", Locale.ENGLISH);
    private static final DateTimeFormatter MES = DateTimeFormatter.ofPattern("MMM yy", Locale.ENGLISH);
    private static final int MAX_PAGINAS = 10;

    /** Nombre en el panel de las páginas que no son fichas. */
    private static final Map<String, String> SECCIONES = Map.ofEntries(
            Map.entry("/", "Home"),
            Map.entry("/workshops", "Workshops"),
            Map.entry("/workshops/firing-services", "Firing services"),
            Map.entry("/workshops/gift-cards", "Gift cards"),
            Map.entry("/calendar", "Calendar"),
            Map.entry("/shop", "Shop"),
            Map.entry("/about", "About"),
            Map.entry("/studio", "Studio & membership"),
            Map.entry("/conditions", "Conditions"),
            Map.entry("/imprint", "Imprint"),
            Map.entry("/privacy-policy", "Privacy policy"));

    private final VisitaDiariaDao visitaDao;
    private final CursoCompraDao cursoCompraDao;
    private final CursoDao cursoDao;
    private final TarjetaRegaloDao tarjetaRegaloDao;
    private final ProductoDao productoDao;

    public EstadisticaService(VisitaDiariaDao visitaDao, CursoCompraDao cursoCompraDao, CursoDao cursoDao,
            TarjetaRegaloDao tarjetaRegaloDao, ProductoDao productoDao) {
        this.visitaDao = visitaDao;
        this.cursoCompraDao = cursoCompraDao;
        this.cursoDao = cursoDao;
        this.tarjetaRegaloDao = tarjetaRegaloDao;
        this.productoDao = productoDao;
    }

    /**
     * Suma la visita al contador del día. No se cuentan robots, páginas que no son públicas
     * ni las visitas del propio admin. Nunca lanza: una visita perdida no debe dar error en la web.
     */
    public void registrarVisita(VisitaDto visita, String userAgent, boolean esAdmin) {
        if (visita == null || esAdmin) return;
        Optional<String> ruta = ClasificadorVisita.ruta(visita.ruta());
        Optional<String> dispositivo = ClasificadorVisita.dispositivo(userAgent);
        if (ruta.isEmpty() || dispositivo.isEmpty()) return;
        try {
            visitaDao.sumarVisita(LocalDate.now(ZONA), ruta.get(),
                    ClasificadorVisita.origen(visita.entrada(), visita.referrer(), visita.utmSource()),
                    dispositivo.get(), ClasificadorVisita.idioma(visita.idioma()));
        } catch (RuntimeException e) {
            logger.warn("No se pudo registrar la visita a {}", ruta.get(), e);
        }
    }

    public EstadisticasDto resumen(String periodo) {
        Periodo p = Periodo.de(periodo, LocalDate.now(ZONA));

        List<VisitaDiaria> filas = visitaDao.findByFechaBetween(p.desdeAnterior(), p.hasta());
        List<VisitaDiaria> actuales = filas.stream().filter(v -> !v.getFecha().isBefore(p.desde())).toList();
        List<VisitaDiaria> anteriores = filas.stream().filter(v -> v.getFecha().isBefore(p.desde())).toList();
        List<VisitaDiaria> entradas = actuales.stream().filter(v -> !ClasificadorVisita.INTERNAL.equals(v.getOrigen())).toList();

        // Serie de visitas (entradas) por día o por mes, alineada con el periodo anterior
        int[] serieActual = new int[p.tramos()];
        int[] serieAnterior = new int[p.tramos()];
        for (VisitaDiaria v : filas) {
            if (ClasificadorVisita.INTERNAL.equals(v.getOrigen())) continue;
            boolean esActual = !v.getFecha().isBefore(p.desde());
            int i = p.tramo(v.getFecha(), esActual);
            if (i >= 0 && i < p.tramos()) (esActual ? serieActual : serieAnterior)[i] += v.getVisitas();
        }

        // Reservas del periodo y del anterior
        List<Object[]> reservas = cursoCompraDao.findCursoYFechaDeReservasEntre(p.desdeAnterior(), p.hasta());
        Map<Long, Integer> reservasPorCurso = new HashMap<>();
        int reservasActuales = 0, reservasAnteriores = 0;
        for (Object[] r : reservas) {
            LocalDate fecha = (LocalDate) r[1];
            if (fecha.isBefore(p.desde())) {
                reservasAnteriores++;
            } else {
                reservasActuales++;
                reservasPorCurso.merge((Long) r[0], 1, Integer::sum);
            }
        }

        // Vistas de cada página (todas, incluidas las navegaciones internas)
        Map<String, Integer> vistasPorRuta = sumar(actuales, VisitaDiaria::getRuta);

        Map<Long, String> cursos = cursoDao.findAll().stream().collect(Collectors.toMap(Curso::getId, c -> nombre(c.getId(), c.getNombre())));
        Map<Long, Integer> vistasPorCurso = new HashMap<>();
        vistasPorRuta.forEach((ruta, n) -> {
            Matcher m = FICHA.matcher(ruta);
            if (m.matches() && m.group(1).equals("workshops")) vistasPorCurso.merge(Long.valueOf(m.group(2)), n, Integer::sum);
        });

        List<EstadisticasDto.Taller> talleres = new ArrayList<>();
        cursos.forEach((id, nombre) -> {
            int vistas = vistasPorCurso.getOrDefault(id, 0);
            int res = reservasPorCurso.getOrDefault(id, 0);
            if (vistas > 0 || res > 0) talleres.add(new EstadisticasDto.Taller(id, nombre, vistas, res));
        });
        talleres.sort(Comparator.comparingInt(EstadisticasDto.Taller::vistas).reversed()
                .thenComparing(Comparator.comparingInt(EstadisticasDto.Taller::reservas).reversed()));

        return new EstadisticasDto(
                p.clave(), p.etiquetas(), toList(serieActual), toList(serieAnterior),
                total(entradas), (int) anteriores.stream().filter(v -> !ClasificadorVisita.INTERNAL.equals(v.getOrigen()))
                        .mapToLong(VisitaDiaria::getVisitas).sum(),
                total(actuales),
                reservasActuales, reservasAnteriores,
                vistasPorCurso.values().stream().mapToInt(Integer::intValue).sum(),
                entradas.stream().filter(v -> ClasificadorVisita.GOOGLE.equals(v.getOrigen())).mapToInt(VisitaDiaria::getVisitas).sum(),
                talleres,
                paginas(vistasPorRuta, cursos),
                ordenadas(sumar(entradas, VisitaDiaria::getOrigen)),
                ordenadas(sumar(entradas, VisitaDiaria::getDispositivo)),
                ordenadas(sumar(entradas, VisitaDiaria::getIdioma)));
    }

    private List<EstadisticasDto.Cuenta> paginas(Map<String, Integer> vistasPorRuta, Map<Long, String> cursos) {
        Map<Long, String> tarjetas = tarjetaRegaloDao.findAll().stream().collect(Collectors.toMap(TarjetaRegalo::getId, t -> nombre(t.getId(), t.getNombre())));
        Map<Long, String> productos = productoDao.findAll().stream().collect(Collectors.toMap(Producto::getId, pr -> nombre(pr.getId(), pr.getNombre())));
        Map<String, Integer> porNombre = new LinkedHashMap<>();
        vistasPorRuta.forEach((ruta, n) -> porNombre.merge(nombrePagina(ruta, cursos, tarjetas, productos), n, Integer::sum));
        return ordenadas(porNombre).stream().limit(MAX_PAGINAS).toList();
    }

    private static String nombrePagina(String ruta, Map<Long, String> cursos, Map<Long, String> tarjetas, Map<Long, String> productos) {
        String seccion = SECCIONES.get(ruta);
        if (seccion != null) return seccion;
        Matcher m = FICHA.matcher(ruta);
        if (m.matches()) {
            Long id = Long.valueOf(m.group(2));
            return switch (m.group(1)) {
                case "workshops" -> "Workshop · " + cursos.getOrDefault(id, "#" + id);
                case "gift-cards" -> "Gift card · " + tarjetas.getOrDefault(id, "#" + id);
                default -> "Product · " + productos.getOrDefault(id, "#" + id);
            };
        }
        return ruta;
    }

    private static String nombre(Long id, String nombre) {
        return nombre == null || nombre.isBlank() ? "#" + id : nombre;
    }

    private static Map<String, Integer> sumar(List<VisitaDiaria> filas, Function<VisitaDiaria, String> clave) {
        Map<String, Integer> r = new HashMap<>();
        for (VisitaDiaria v : filas) r.merge(clave.apply(v), v.getVisitas(), Integer::sum);
        return r;
    }

    private static List<EstadisticasDto.Cuenta> ordenadas(Map<String, Integer> mapa) {
        return mapa.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed().thenComparing(Map.Entry.comparingByKey()))
                .map(e -> new EstadisticasDto.Cuenta(e.getKey(), e.getValue()))
                .toList();
    }

    private static int total(List<VisitaDiaria> filas) {
        return filas.stream().mapToInt(VisitaDiaria::getVisitas).sum();
    }

    private static List<Integer> toList(int[] a) {
        List<Integer> l = new ArrayList<>(a.length);
        for (int x : a) l.add(x);
        return l;
    }

    /** Periodo pedido y el anterior de la misma duración: 7 o 30 días (por día) o 12 meses (por mes). */
    record Periodo(String clave, LocalDate desde, LocalDate hasta, LocalDate desdeAnterior, boolean mensual, int tramos) {

        static Periodo de(String clave, LocalDate hoy) {
            if ("12m".equals(clave)) {
                LocalDate desde = YearMonth.from(hoy).minusMonths(11).atDay(1);
                return new Periodo("12m", desde, hoy, desde.minusMonths(12), true, 12);
            }
            int dias = "7d".equals(clave) ? 7 : 30;
            LocalDate desde = hoy.minusDays(dias - 1L);
            return new Periodo(dias == 7 ? "7d" : "30d", desde, hoy, desde.minusDays(dias), false, dias);
        }

        /** Posición de una fecha en la serie (la del periodo anterior se alinea con la actual). */
        int tramo(LocalDate fecha, boolean actual) {
            LocalDate inicio = actual ? desde : desdeAnterior;
            if (mensual) {
                return (int) java.time.temporal.ChronoUnit.MONTHS.between(YearMonth.from(inicio), YearMonth.from(fecha));
            }
            return (int) java.time.temporal.ChronoUnit.DAYS.between(inicio, fecha);
        }

        List<String> etiquetas() {
            List<String> l = new ArrayList<>(tramos);
            for (int i = 0; i < tramos; i++) {
                l.add(mensual ? desde.plusMonths(i).format(MES) : desde.plusDays(i).format(DIA));
            }
            return l;
        }
    }
}

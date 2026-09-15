package com.ashyaart.ashya_art_backend.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import jakarta.persistence.EntityNotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.ashyaart.ashya_art_backend.assembler.ClienteAssembler;
import com.ashyaart.ashya_art_backend.assembler.TarjetaRegaloCompraAssembler;
import com.ashyaart.ashya_art_backend.entity.Cliente;
import com.ashyaart.ashya_art_backend.entity.Compra;
import com.ashyaart.ashya_art_backend.entity.TarjetaRegalo;
import com.ashyaart.ashya_art_backend.entity.TarjetaRegaloCompra;
import com.ashyaart.ashya_art_backend.event.CompraEventos.TarjetaRegaloCompradaEvent;
import com.ashyaart.ashya_art_backend.filter.TarjetaRegaloCompraFilter;
import com.ashyaart.ashya_art_backend.model.ClienteDto;
import com.ashyaart.ashya_art_backend.model.TarjetaRegaloAdminCreacionDto;
import com.ashyaart.ashya_art_backend.model.TarjetaRegaloCompraDto;
import com.ashyaart.ashya_art_backend.model.TarjetaRegaloCompraEdicionDto;
import com.ashyaart.ashya_art_backend.repository.ClienteDao;
import com.ashyaart.ashya_art_backend.repository.CompraDao;
import com.ashyaart.ashya_art_backend.repository.TarjetaRegaloCompraDao;
import com.ashyaart.ashya_art_backend.repository.TarjetaRegaloDao;

@Service
public class TarjetaRegaloCompraService {

    private static final Logger logger = LoggerFactory.getLogger(TarjetaRegaloCompraService.class);

    @Autowired
    private TarjetaRegaloCompraDao tarjetaRegaloCompraDao;

    @Autowired
    private TarjetaRegaloDao tarjetaRegaloDao;

    @Autowired
    private ClienteDao clienteDao;

    @Autowired
    private CompraDao compraDao;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    public List<TarjetaRegaloCompraDto> findByFilter(TarjetaRegaloCompraFilter filter) {
        logger.info("findByFilter - Iniciando búsqueda de compras de tarjetas regalo");
        List<TarjetaRegaloCompra> compras = tarjetaRegaloCompraDao.findByFilter(filter.getIdCliente());

        List<TarjetaRegaloCompraDto> resultado = compras.stream()
                .map(TarjetaRegaloCompraAssembler::toDto)
                .toList();

        logger.info("findByFilter - Se encontraron {} compras de tarjetas regalo", resultado.size());
        return resultado;
    }

    @Transactional
    public void canjear(Long id) {
        logger.info("canjear - Intentando canjear compra ID {}", id);

        // Validación de existencia
        TarjetaRegaloCompra compra = tarjetaRegaloCompraDao.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Compra de tarjeta regalo no encontrada con ID: " + id));

        if (compra.isCanjeada()) {
            logger.info("canjear - La compra ID {} ya estaba canjeada. No se realizan cambios.", id);
            return;
        }

        int actualizados = tarjetaRegaloCompraDao.canjearPorId(id);
        if (actualizados == 0) {
            // Caso raro: condición de carrera o no cumple el WHERE
            throw new IllegalStateException("No se pudo canjear la compra con ID: " + id);
        }
        logger.info("canjear - Compra ID {} canjeada correctamente", id);
    }

    @Transactional
    public TarjetaRegaloCompraDto actualizarCanjeoManual(Long id, TarjetaRegaloCompraEdicionDto dto) {
        logger.info("actualizarCanjeoManual - Editando canjeo manual de compra ID {}", id);

        TarjetaRegaloCompra compra = tarjetaRegaloCompraDao.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Compra de tarjeta regalo no encontrada con ID: " + id));

        boolean canjeada = Boolean.TRUE.equals(dto.getCanjeada());

        if (canjeada && dto.getFechaBaja() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "La fecha de canjeo (Redeemed On) es obligatoria al marcar la tarjeta como canjeada.");
        }

        compra.setCanjeada(canjeada);
        compra.setEstado(!canjeada);
        compra.setFechaBaja(canjeada ? dto.getFechaBaja() : null);
        if (!canjeada) {
            // Ya no está canjeada: el importe utilizado deja de tener sentido.
            compra.setMontoUtilizado(null);
        }

        TarjetaRegaloCompra guardada = tarjetaRegaloCompraDao.save(compra);
        logger.info("actualizarCanjeoManual - Compra ID {} actualizada correctamente", id);
        return TarjetaRegaloCompraAssembler.toDto(guardada);
    }

    @Transactional
    public void eliminarLogico(Long id) {
        logger.info("eliminarLogico - Intentando desactivar (borrado lógico) compra ID {}", id);

        if (!tarjetaRegaloCompraDao.existsById(id)) {
            logger.warn("eliminarLogico - Compra con ID {} no encontrada", id);
            throw new EntityNotFoundException("Compra de tarjeta regalo no encontrada con ID: " + id);
        }

        int actualizados = tarjetaRegaloCompraDao.desactivarPorId(id);
        if (actualizados == 0) {
            throw new IllegalStateException("No se pudo desactivar la compra con ID: " + id);
        }
        logger.info("eliminarLogico - Compra ID {} desactivada correctamente", id);
    }

    /**
     * Crea una tarjeta regalo a mano desde el panel de administración, como cortesía de la tienda
     * (sin pasar por Stripe ni cobrar nada): genera una Compra con total 0€ ya pagada y la
     * TarjetaRegaloCompra asociada, reutilizando el precio de catálogo de la plantilla elegida.
     */
    @Transactional
    public TarjetaRegaloCompraDto crearManual(TarjetaRegaloAdminCreacionDto dto) {
        logger.info("crearManual - Creando tarjeta regalo manual desde el panel de administración");

        if (dto.getIdTarjetaRegalo() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Debe seleccionarse una tarjeta regalo del catálogo.");
        }

        TarjetaRegalo plantilla = tarjetaRegaloDao.findById(dto.getIdTarjetaRegalo())
                .orElseThrow(() -> new EntityNotFoundException("Tarjeta regalo no encontrada con ID: " + dto.getIdTarjetaRegalo()));

        Cliente cliente = resolverCliente(dto);

        Compra compra = new Compra();
        compra.setCliente(cliente);
        compra.setCodigoCompra(UUID.randomUUID().toString());
        compra.setFechaCompra(LocalDate.now());
        compra.setTotal(BigDecimal.ZERO);
        compra.setPagado(true);
        compra = compraDao.save(compra);

        String codigoUnico = UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();

        TarjetaRegaloCompra tarjetaCompra = new TarjetaRegaloCompra();
        tarjetaCompra.setCodigo(codigoUnico);
        tarjetaCompra.setDestinatario(dto.getDestinatario());
        tarjetaCompra.setTarjetaRegalo(plantilla);
        tarjetaCompra.setCliente(cliente);
        tarjetaCompra.setCompra(compra);
        tarjetaCompra.setCanjeada(false);
        tarjetaCompra.setEstado(true);
        tarjetaCompra.setFechaCompra(LocalDate.now());
        tarjetaCompra.setFechaCaducidad(LocalDate.now().plusMonths(6));
        tarjetaCompra.setPrecio(plantilla.getPrecio());
        tarjetaCompra = tarjetaRegaloCompraDao.save(tarjetaCompra);

        eventPublisher.publishEvent(
                new TarjetaRegaloCompradaEvent(
                    cliente.getEmail(),
                    cliente.getNombre(),
                    dto.getDestinatario(),
                    codigoUnico,
                    plantilla.getPrecio(),
                    tarjetaCompra.getFechaCaducidad()
                )
        );

        logger.info("crearManual - Tarjeta regalo {} creada correctamente para {}", codigoUnico, cliente.getEmail());
        return TarjetaRegaloCompraAssembler.toDto(tarjetaCompra);
    }

    private Cliente resolverCliente(TarjetaRegaloAdminCreacionDto dto) {
        if (dto.getIdCliente() != null) {
            return clienteDao.findById(dto.getIdCliente())
                    .orElseThrow(() -> new EntityNotFoundException("Cliente no encontrado con ID: " + dto.getIdCliente()));
        }

        ClienteDto clienteNuevo = dto.getClienteNuevo();
        if (clienteNuevo == null || clienteNuevo.getEmail() == null || clienteNuevo.getEmail().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Debe seleccionarse un cliente existente o indicar los datos de un cliente nuevo.");
        }

        Cliente existente = clienteDao.findByEmail(clienteNuevo.getEmail());
        if (existente != null) {
            return existente;
        }

        Cliente cliente = ClienteAssembler.toEntity(clienteNuevo);
        cliente.setId(null);
        cliente.setFechaAlta(LocalDate.now());
        return clienteDao.save(cliente);
    }
}

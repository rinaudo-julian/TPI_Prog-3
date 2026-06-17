package com.utn.backend.controller;

import com.utn.backend.exception.GlobalHandlerException;
import com.utn.backend.service.impl.PedidoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PedidoController.class)
@Import(GlobalHandlerException.class)
class PedidoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PedidoService pedidoService;

    @Test
    void createShouldReturn400WhenEstadoIsNull() throws Exception {
        mockMvc.perform(post("/pedidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "formaPago": "TARJETA",
                                  "idUsuario": 1,
                                  "detallePedido": [
                                    {"idProducto": 1, "cantidad": 2}
                                  ]
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Error de validación"))
                .andExpect(jsonPath("$.errors").value(hasItem("estado: El estado es obligatorio")));

        verifyNoInteractions(pedidoService);
    }

    @Test
    void createShouldReturn400WhenFormaPagoIsNull() throws Exception {
        mockMvc.perform(post("/pedidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "estado": "PENDIENTE",
                                  "idUsuario": 1,
                                  "detallePedido": [
                                    {"idProducto": 1, "cantidad": 2}
                                  ]
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Error de validación"))
                .andExpect(jsonPath("$.errors").value(hasItem("formaPago: La forma de pago es obligatoria")));

        verifyNoInteractions(pedidoService);
    }

    @Test
    void createShouldReturn400WhenIdUsuarioIsNull() throws Exception {
        mockMvc.perform(post("/pedidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "estado": "PENDIENTE",
                                  "formaPago": "TARJETA",
                                  "detallePedido": [
                                    {"idProducto": 1, "cantidad": 2}
                                  ]
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Error de validación"))
                .andExpect(jsonPath("$.errors").value(hasItem("idUsuario: El usuario es obligatorio")));

        verifyNoInteractions(pedidoService);
    }

    @Test
    void createShouldReturn400WhenDetallePedidoIsNull() throws Exception {
        mockMvc.perform(post("/pedidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "estado": "PENDIENTE",
                                  "formaPago": "TARJETA",
                                  "idUsuario": 1
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Error de validación"))
                .andExpect(jsonPath("$.errors").value(hasItem("detallePedido: Se requiere al menos un detalle")));

        verifyNoInteractions(pedidoService);
    }

    @Test
    void createShouldReturn400WhenDetallePedidoIsEmpty() throws Exception {
        mockMvc.perform(post("/pedidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "estado": "PENDIENTE",
                                  "formaPago": "TARJETA",
                                  "idUsuario": 1,
                                  "detallePedido": []
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Error de validación"))
                .andExpect(jsonPath("$.errors").value(hasItem("detallePedido: Se requiere al menos un detalle")));

        verifyNoInteractions(pedidoService);
    }

    @Test
    void createShouldReturn400WhenDetallePedidoItemHasNullProductId() throws Exception {
        mockMvc.perform(post("/pedidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "estado": "PENDIENTE",
                                  "formaPago": "TARJETA",
                                  "idUsuario": 1,
                                  "detallePedido": [
                                    {"cantidad": 2}
                                  ]
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Error de validación"))
                .andExpect(jsonPath("$.errors").value(hasItem("detallePedido[0].idProducto: El producto es obligatorio")));

        verifyNoInteractions(pedidoService);
    }

    @Test
    void createShouldReturn400WhenDetallePedidoItemHasNullCantidad() throws Exception {
        mockMvc.perform(post("/pedidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "estado": "PENDIENTE",
                                  "formaPago": "TARJETA",
                                  "idUsuario": 1,
                                  "detallePedido": [
                                    {"idProducto": 1}
                                  ]
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Error de validación"))
                .andExpect(jsonPath("$.errors").value(hasItem("detallePedido[0].cantidad: La cantidad es obligatoria")));

        verifyNoInteractions(pedidoService);
    }

    @Test
    void createShouldReturn400WhenDetallePedidoItemHasInvalidCantidad() throws Exception {
        mockMvc.perform(post("/pedidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "estado": "PENDIENTE",
                                  "formaPago": "TARJETA",
                                  "idUsuario": 1,
                                  "detallePedido": [
                                    {"idProducto": 1, "cantidad": 0}
                                  ]
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Error de validación"))
                .andExpect(jsonPath("$.errors").value(hasItem("detallePedido[0].cantidad: La cantidad debe ser al menos 1")));

        verifyNoInteractions(pedidoService);
    }
}

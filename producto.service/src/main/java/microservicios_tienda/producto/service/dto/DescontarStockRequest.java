package microservicios_tienda.producto.service.dto;

public class DescontarStockRequest {
    private Integer cantidad;
    public DescontarStockRequest(Integer cantidad){
        this.cantidad=cantidad;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }


}

package GlobantForeCast.Controlador;

import GlobantForeCast.Forecast.CRUD.Visualizar.VisualizarPronostico;
import GlobantForeCast.Forecast.ForeCast;
import GlobantForeCast.Modelo.Entity.Enlace.EnlaceTrabajador;
import GlobantForeCast.Modelo.Entity.Forecast.Demanda;
import GlobantForeCast.Modelo.Entity.Trabajador;
import GlobantForeCast.Query.CRUD.Crear.InsertarTrabajador;
import GlobantForeCast.Query.CRUD.Visualizar.VisualizarTrabajador;
import GlobantForeCast.Validaciones.Conversion.CalcularPatrocinados;
import GlobantForeCast.Validaciones.MesesAnio.ValidarMesDelAnio;
import jakarta.servlet.http.HttpServlet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@RestController
public class ControladorTrabajadores extends HttpServlet {

    @Autowired
    private InsertarTrabajador insertarTrabajador;
    @Autowired
    private VisualizarTrabajador visualizarTrabajador;
    @Autowired
    private ValidarMesDelAnio validarMesDelAnio;
    @Autowired
    private ForeCast foreCast;
    @Autowired
    private CalcularPatrocinados calcularPatrocinados;
    @Autowired
    private VisualizarPronostico visualizarPronostico;
    private List<String> listaMeses = new ArrayList<>();
    private List<Integer> listaTrabajadoresXMes = new ArrayList<>();

    @PostMapping("/crearTrabajador")
    public ResponseEntity<?> crearTrabajador (@RequestBody EnlaceTrabajador enlaceTrabajador){
        Integer cantidadTrbajadores = enlaceTrabajador.getNumeroTrabajadores();
        LocalDate fechaIngreso = enlaceTrabajador.getFechaIngreso();
        for(int i = 0; i < cantidadTrbajadores; i++){
            Trabajador trabajador = new Trabajador();
            trabajador.setNombrecompleto("Trabajador Generado Automatico");
            trabajador.setFechaIngreso(fechaIngreso);
            this.insertarTrabajador.crearTrabajador(trabajador);
        }
        return ResponseEntity.ok().build();
    }

    @GetMapping("/cantidadTrabajadores")
    public ResponseEntity<?> cantidadTrabajadores (){
        Long cantidadTrabajadores = this.visualizarTrabajador.contarTrabajadores();
        return ResponseEntity.ok(cantidadTrabajadores);
    }

    @GetMapping("/consultarPatrocinados/{mes}")
    public ResponseEntity<?> consultarPatrocinados(@PathVariable String mes){

        int numeroMes = validarMesDelAnio.validarMesDelAnio(mes);
        List<Demanda> listaPronosticoPorMes = visualizarPronostico.consultarDemandaPorMes(numeroMes);


        Integer total = calcularPatrocinados
                .cantidadPatrocinios(listaPronosticoPorMes);





        return ResponseEntity.ok(total);
    }

    @GetMapping("/consultarTrabajadoresPorForecast/{mes}")
    public ResponseEntity<?> consultarTrabajadoresPorForecast (@PathVariable String mes){

        int numeroMes = validarMesDelAnio.validarMesDelAnio(mes);
        List<Trabajador> listaTrabajadoresPorMes = visualizarTrabajador.cantidadTrabajadoresPorMes(numeroMes);

        listaMeses.add(mes);

        listaTrabajadoresXMes.add(listaTrabajadoresPorMes.size());


        if (listaMeses.size() == 12){

            foreCast.agregarCantidadTrabajadores(listaTrabajadoresXMes, listaMeses);
        }

        return ResponseEntity.ok(listaTrabajadoresPorMes.size());
    }

}

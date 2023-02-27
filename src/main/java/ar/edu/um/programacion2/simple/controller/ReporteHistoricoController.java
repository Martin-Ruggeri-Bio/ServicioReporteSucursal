/**
 * 
 */
package ar.edu.um.programacion2.simple.controller;

import ar.edu.um.programacion2.simple.dtos.Message;
import ar.edu.um.programacion2.simple.model.ReporteHistorico;
import ar.edu.um.programacion2.simple.model.User;
import ar.edu.um.programacion2.simple.service.ReporteHistoricoService;
import ar.edu.um.programacion2.simple.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;


@RestController
@RequestMapping("/Historicos")
public class ReporteHistoricoController {

	@Autowired
	private ReporteHistoricoService reporteHistoricoService;

	@Autowired
	private UserService userService;


	@PostMapping("/addReporte")
    public ResponseEntity<Message> addMenu(@RequestHeader("Authorization") String tokenHeader,
        @Valid @RequestBody ReporteHistorico reporteHistorico, BindingResult bindingResult){
            String token = tokenHeader.replace("Bearer ", "");
            if (token == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            Optional<User> userOptional = userService.getByToken(token);
            User user = userOptional.orElse(null);
            
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            if (bindingResult.hasErrors())
                return new ResponseEntity<>(new Message("Revise los campos"),HttpStatus.BAD_REQUEST);
            this.reporteHistoricoService.add(reporteHistorico);
            return new ResponseEntity<>(new Message("Reporte Historico agregado"),HttpStatus.OK);
    }

}

package angularjs_spring_mvc.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;

import angularjs_spring_mvc.dao.DaoImplementacao;
import angularjs_spring_mvc.dao.DaoInterface;
import angularjs_spring_mvc.model.ItemPedido;
import angularjs_spring_mvc.model.Pedido;
import angularjs_spring_mvc.model.PedidoBean;

@Controller
@RequestMapping(value="/pedido")
public class PedidoController extends DaoImplementacao<Pedido> implements DaoInterface<Pedido>{
	
	@Autowired
	private ItemPedidoController itemPedidoController;
	
	public PedidoController(Class<Pedido> persistenceClass) {
		super (persistenceClass);
	}
	
	@RequestMapping(value="finalizar", method=RequestMethod.POST)
	@ResponseBody
	public String finalizar(@RequestBody String jsonPedido) throws Exception {
		
		PedidoBean pedidoBean = new Gson().fromJson(jsonPedido, PedidoBean.class);
		
		Pedido pedido = pedidoBean.getPedido();
		
		pedido = super.merge(pedido);
		
		List<ItemPedido> itemPedidos = pedidoBean.getItens();
		
		for (ItemPedido itemPedido : itemPedidos) {
			itemPedido.setPedido(pedido);
			itemPedidoController.salvar(itemPedido);
		}
		return pedido.getId().toString();
	}
	
	@RequestMapping(value = "listar", method = RequestMethod.GET, headers = "Accept=application/json")
	@ResponseBody
	public String listar() throws Exception {
		return new Gson().toJson(super.lista());
	}
	
	@RequestMapping(value="deletar/{codPedido}", method=RequestMethod.DELETE)
	public @ResponseBody String deletar(@PathVariable("codPedido") String codPedido) throws Exception{
		List<ItemPedido> itemPedidos = itemPedidoController.lista("pedido.id", Long.parseLong(codPedido));
		for (ItemPedido itemPedido : itemPedidos) {
			itemPedidoController.deletar(itemPedido);
		}
		super.deletar(loadObjeto(Long.parseLong(codPedido)));
		return new Gson().toJson(super.lista());
	}

}

package com.kupanet.cashiersystem.web;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.constraints.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.kupanet.cashiersystem.model.Order;
import com.kupanet.cashiersystem.service.OrderService;

@Controller
@Validated
public class OrderController {
	  private final Logger logger = LoggerFactory.getLogger(OrderController.class);
	  @Autowired
	  private OrderService orderService;
	  
	  @RequestMapping(value="/", method = RequestMethod.GET)
	  public  String homepage() {
	    return "home";
	  }
	  
	  @RequestMapping(value="/querybybumbers", method = RequestMethod.GET)
	  public  String queryOrderNumbers(
              @Pattern(regexp = "^(0?[1-9]|10|11),(0?[1-9]|10|11)"
              		+ ",(0?[1-9]|10|11),(0?[1-9]|10|11),(0?[1-9]|10|11)$", message = "format error,fill in  \"XX,XX,XX,XX,XX\"")
			  @RequestParam(value="numbers",required=true,defaultValue="0") 
              String numbers,
			  Model model) {
		if(numbers==null||numbers.isEmpty()) return "notFound";
		List<Order> findedOrder = orderService.queryByNumbers(numbers);
		if(findedOrder==null||findedOrder.isEmpty()) return "notFound";
		else{
			List<ArrayList<Order>> resultlist= new ArrayList<ArrayList<Order>>();
			for(Order order : findedOrder) {
				ArrayList<Order> s = new ArrayList<Order>();
				for(int i=-2;i<=1;i++){
					Order nearCase = orderService.queryById(order.getId()+i);
					if(nearCase!=null) s.add(nearCase);
				}
				if(!s.isEmpty()) {
					logger.warn("resultlist.size():"+resultlist.size());
					resultlist.add(s);
				}
			}
			model.addAttribute("querybybumbers",resultlist);
			return "querybybumbers";
	    }
	  }
	  
	  @RequestMapping(value="/querytwoterm", method = RequestMethod.GET)
	  public  String querylastOrderNumbers(
              @Pattern(regexp = "^(0?[1-9]|10|11),(0?[1-9]|10|11)"
                		+ ",(0?[1-9]|10|11),(0?[1-9]|10|11),(0?[1-9]|10|11)$", message = "format error,fill in  \"XX,XX,XX,XX,XX\"")
			  @RequestParam(value="beforenum",required=true,defaultValue="0") String beforenum,
              @Pattern(regexp = "^(0?[1-9]|10|11),(0?[1-9]|10|11)"
                		+ ",(0?[1-9]|10|11),(0?[1-9]|10|11),(0?[1-9]|10|11)$", message = "format error,fill in  \"XX,XX,XX,XX,XX\"")
			  @RequestParam(value="currentnum",required=true,defaultValue="0") String currentnum,Model model) {
		if(beforenum==null||beforenum.isEmpty()||currentnum==null||currentnum.isEmpty()) return "notFound";
		logger.warn("currentnum:"+currentnum+" beforenum:"+beforenum);
		List<Order> findedOrder = orderService.queryLastByNumbers(currentnum);
		if(findedOrder==null||findedOrder.isEmpty()) return "notFound";
		else{
			List<ArrayList<Order>> resultlist= new ArrayList<ArrayList<Order>>();
			for(Order order : findedOrder) {
				String[] strs=order.getNumbers().split(",");
			    List<String> nums = Arrays.asList(strs);
			    String[] befstrs=beforenum.split(",");
			    int j=0;
			    for(int i=0;i<befstrs.length;i++){
			    	if(nums.contains(befstrs[i])) j++;
			    }
			    if(j>=4){
					ArrayList<Order> s = new ArrayList<Order>();
					for(int i=-2;i<=2;i++){
						Order nearCase = orderService.queryById(order.getId()+i);
						if(nearCase!=null) s.add(nearCase);
					}
					if(!s.isEmpty()) {
						logger.warn("tiyh-----------resultlist.size():"+resultlist.size());
						resultlist.add(s);
					}
			    	
			    }
			}
			model.addAttribute("querybybumbers",resultlist);
			return "querybybumbers";
	    }
	  }
	  
	  @RequestMapping(value="/querybyid", method = RequestMethod.GET)
	  public  String queryLastById(@RequestParam(value="id",required=true,defaultValue="0") String id,
			  Model model) {
		int term=0;
		try {
			term = Integer.parseInt(id);
		} catch (NumberFormatException e) {
		    e.printStackTrace();
		}
		if(term==0){
			return "notFound";
		}
		Order findedOrder = orderService.queryById(term);
		if(findedOrder==null){
			return "notFound";
		}
		else{
			List<Order> list= new ArrayList<Order>();
			for(int i=-2;i<=1;i++){
				Order temp = orderService.queryById(term+i);
				if(temp!=null){
					list.add(temp);
				}
			}
			model.addAttribute("querybyid",list);
			return "querybyid";
	    }
	  }
	  
	  @RequestMapping(value="/countnumbers", method = RequestMethod.GET)
	  public  String countOrderNumbers(
              @Pattern(regexp = "^(0?[1-9]|10|11),(0?[1-9]|10|11)"
                		+ ",(0?[1-9]|10|11),(0?[1-9]|10|11),(0?[1-9]|10|11)$", message = "format error,fill in  \"XX,XX,XX,XX,XX\"")
			  @RequestParam(value="currentnum",required=true,defaultValue="0") String currentnum,
			  Model model) {
		if(currentnum==null||currentnum.isEmpty()) return "notFound";
		logger.warn("currentnum:"+currentnum);
		List<Order> nextOrder = orderService.queryNextByNumbers(currentnum);
		int sum = orderService.countNextByNumbers(currentnum);
		if(nextOrder==null||nextOrder.isEmpty()||sum<=0) return "notFound";
		else{
			HashMap<String,Integer> resultMap= new HashMap<String,Integer>();
			for(Order order : nextOrder) {
				String[] everyStrs=order.getNumbers().split(",");
			    for(int i=0;i<everyStrs.length;i++) {
			    	for(int k=i+1;k<everyStrs.length;k++) {
			    			String key = everyStrs[i]+','+everyStrs[k];
			    			int value = resultMap.getOrDefault(key, 0);
			    			resultMap.put(key, value+1);
			    	}
			    }
			}
			List<Map.Entry<String, Integer>> entryArrayList = new ArrayList<>(resultMap.entrySet());
			Collections.sort(entryArrayList, (o1, o2) -> o2.getValue().compareTo(o1.getValue()));
			model.addAttribute("sum",sum);
			model.addAttribute("resultmap",entryArrayList);
			return "countnumbers";
	    }
	  }
	  
	  @RequestMapping(value="/countthreenumbers", method = RequestMethod.GET)
	  public  String countThreeNumbers(
              @Pattern(regexp = "^(0?[1-9]|10|11),(0?[1-9]|10|11)"
                		+ ",(0?[1-9]|10|11),(0?[1-9]|10|11),(0?[1-9]|10|11)$", message = "format error,fill in  \"XX,XX,XX,XX,XX\"")
			  @RequestParam(value="currentnum",required=true,defaultValue="0") String currentnum,
			  Model model) {
		if(currentnum==null||currentnum.isEmpty()) return "notFound";
		logger.warn("currentnum:"+currentnum);
		List<Order> nextOrder = orderService.queryNextByNumbers(currentnum);
		int sum = orderService.countNextByNumbers(currentnum);
		if(nextOrder==null||nextOrder.isEmpty()||sum<=0) return "notFound";
		else{
			HashMap<String,Integer> resultMap= new HashMap<String,Integer>();
			for(Order order : nextOrder) {
				String[] everyStrs=order.getNumbers().split(",");
			    for(int i=0;i<everyStrs.length;i++) {
			    	for(int k=i+1;k<everyStrs.length;k++) {
			    		for(int m=k+1;m<everyStrs.length;m++) {
			    			String key = everyStrs[i]+','+everyStrs[k]+','+everyStrs[m];
			    			int value = resultMap.getOrDefault(key, 0);
			    			resultMap.put(key, value+1);
			    		}
			    	}
			    }
			}
			List<Map.Entry<String, Integer>> entryArrayList = new ArrayList<>(resultMap.entrySet());
			Collections.sort(entryArrayList, (o1, o2) -> o2.getValue().compareTo(o1.getValue()));
			model.addAttribute("sum",sum);
			model.addAttribute("resultmap",entryArrayList);
			return "countnumbers";
	    }
	  }
	  

	  @RequestMapping(value="/querytwotermstaticsic", method = RequestMethod.GET)
	  public  String twoNumbersStatistics(
			  @Pattern(regexp = "^[0-9]*$", message = "format error,number only")
			  @RequestParam(value="startid",required=true,defaultValue="18070635") String startid,
			  @RequestParam(value="endid",required=true,defaultValue="18070735") String endid ,Model model) {
		int startId = Integer.parseInt(startid);
		int endId = Integer.parseInt(endid);
		int sumtimes = 1;
		int correcttimes = 0;
		for(;endId>startId ;endId--) {
			if(endId%100<=2) {
				endId = endId-100+63;
				continue;
			}
			Order predictnumOrder = orderService.queryById(endId);
			Order currentnumOrder = orderService.queryById(endId-1);
			Order beforenumOrder = orderService.queryById(endId-2);
			if(predictnumOrder==null||currentnumOrder==null||beforenumOrder==null)
				continue;
			String predictnum = predictnumOrder.getNumbers();
			String currentnum = currentnumOrder.getNumbers();
			String beforenum = beforenumOrder.getNumbers();
			if(predictnum==null||currentnum==null||beforenum==null
					||predictnum.isEmpty()||currentnum.isEmpty()||beforenum.isEmpty())
				continue;
			List<Order> findedOrder = orderService.queryLastByNumbers(currentnum);
			if(findedOrder==null||findedOrder.isEmpty()) continue;
			for(Order order : findedOrder) {
				String[] strs=order.getNumbers().split(",");
				List<String> nums = Arrays.asList(strs);
				String[] befstrs=beforenum.split(",");
				int j=0;
				for(int i=0;i<befstrs.length;i++){
				    if(nums.contains(befstrs[i])) j++;
				}
				if(j>=4){
					Order nearCase = orderService.queryById(order.getId()+2);
					if(nearCase!=null&&nearCase.getId()<endId) {
						sumtimes ++;
						String[] strs2=nearCase.getNumbers().split(",");
						List<String> nums2 = Arrays.asList(strs2);
						String[] befstrs2=predictnum.split(",");
						int p=0;
						for(int i=0;i<befstrs2.length;i++){
						    if(nums2.contains(befstrs2[i])) p++;
						}
						logger.warn("before--correct-----------------nearCase.getNumbers():-------"+nearCase.getNumbers()
						    +"-------predictnum:"+predictnum);
						if(p==2) {
						    correcttimes++;
						}
					}
				}
		    }
		}
		double freq = correcttimes/(double)sumtimes;
		model.addAttribute("sum",sumtimes);
		model.addAttribute("correct",correcttimes);
		model.addAttribute("freq",freq);
		logger.warn("final--------------------------freq:"+freq);
		return "countnumbers";
	  }
}

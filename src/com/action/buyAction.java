package com.action;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.ServletActionContext;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;

import com.dao.TCatelogDAO;
import com.dao.TGoodsDAO;
import com.dao.TOrderDAO;
import com.dao.TOrderItemDAO;


import com.model.TGoods;
import com.model.TOrder;
import com.model.TOrderItem;

import com.model.TUser;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import com.util.Cart;

public class buyAction extends ActionSupport
{
	private int goodsId;
	private int quantity;
	
	private int orderId;
	
	private TGoodsDAO goodsDAO;
	private TOrderDAO orderDAO;
	private TOrderItemDAO orderItemDAO;
	
	private String message;
	private String path;
	
	private String odderSonghuodizhi;
	private String odderFukuangfangshi;
	

	
	public String addToCart()
	{
		TGoods goods=goodsDAO.findById(goodsId);
		TOrderItem orderItem=new TOrderItem();
		orderItem.setGoods(goods);
		orderItem.setGoodsQuantity(quantity);
		
		Map session= ServletActionContext.getContext().getSession();
		Cart cart = (Cart)session.get("cart");
		cart.addGoods(goodsId, orderItem);
		session.put("cart",cart);
		//this.setMessage("");
		this.setPath("myCart.action");
		return "succeed";
	}
	
	
	
	public String myCart()
	{
		return ActionSupport.SUCCESS;
	}
	

	public String orderQueren()
	{
		Map request=(Map)ServletActionContext.getContext().get("request");
		
		return ActionSupport.SUCCESS;
	}
	
	
	public String orderSubmit()
	{
		Map session= ServletActionContext.getContext().getSession();
		Cart cart = (Cart)session.get("cart");
		TUser user=(TUser)session.get("user");
		TOrder order=new TOrder();
		
		order.setOrderBianhao(new SimpleDateFormat("yyyyMMddhhmmss").format(new Date()));
		order.setOrderDate(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
		order.setOdderSonghuodizhi(odderSonghuodizhi);
		order.setOdderFukuangfangshi("��������");
		order.setfkstatus("δ����");
		order.setOrderJine(cart.getTotalPrice());
		order.setOrderUserId(user.getUserId());
		
		
		orderDAO.save(order);
		
		for (Iterator it = cart.getItems().values().iterator(); it.hasNext();)
		{

			TOrderItem orderItem = (TOrderItem) it.next();
			orderItem.setOrderId(order.getOrderId());
			orderItem.setGoodsId(orderItem.getGoods().getGoodsId());
			orderItem.setZt("������");
			orderItemDAO.save(orderItem);
			

			
			
			
	
			
		}
		
		cart.getItems().clear();
		session.put("cart", cart);
		
		
		
		
		Map request=(Map)ServletActionContext.getContext().get("request");
		request.put("order", order);
		
		return ActionSupport.SUCCESS;
		
	}
	
	
	
	public String myOrder()
	{
		Map session= ServletActionContext.getContext().getSession();
		TUser user=(TUser)session.get("user");
		String sql="from TOrder where orderUserId="+user.getUserId();
		List orderList=orderDAO.getHibernateTemplate().find(sql);
		Map request=(Map)ServletActionContext.getContext().get("request");
		request.put("orderList", orderList);
		
		return ActionSupport.SUCCESS;
	}
	public String orderShouli(){
		TOrderItem orderItem=orderItemDAO.findById(orderId);
		orderItem.setZt("���ջ�");
		orderItemDAO.attachDirty(orderItem);
	
		this.setMessage("�ջ��ɹ�");
		this.setPath("orderDetail.action?orderId="+orderItem.getOrderId());
		return "succeed";
	}
	
	
	
	public String orderDel()//�û��Լ�ɾ������
	{
		TOrder order=orderDAO.findById(orderId);
		orderDAO.delete(order);
		
		
		Map session= ServletActionContext.getContext().getSession();
		TUser user=(TUser)session.get("user");
		
		this.setMessage("ɾ���ɹ�");
		this.setPath("myOrder.action?userId="+user.getUserId());
		return "succeed";
	}
	
		
	
	public String orderDetail()
	{
		String sql="from TOrderItem where orderId="+orderId;
		List orderItemList=orderItemDAO.getHibernateTemplate().find(sql);
		for(int i=0;i<orderItemList.size();i++)
		{
			TOrderItem orderItem=(TOrderItem)orderItemList.get(i);
			orderItem.setGoods(goodsDAO.findById(orderItem.getGoodsId()));
		}
		Map request=(Map)ServletActionContext.getContext().get("request");
		request.put("orderItemList", orderItemList);
		return ActionSupport.SUCCESS;
	}
	
	

	
	//ע���û�����
	public String orderFk(){

		System.out.println("\nbookCase*********************action="+orderId);
		
		TOrder Order=orderDAO.findById(orderId);


		Order.setfkstatus("�Ѹ���");
		
		orderDAO.attachDirty(Order);
		
		Map request=(Map)ServletActionContext.getContext().get("request");
		
		this.setMessage("֧���ɹ�");
		this.setPath("myOrder.action");
		return "succeed";
		

	}
	//��������
	public String orderHdFk(){
		TOrder Order=orderDAO.findById(orderId);
		System.out.println("\nbookCase*********************action="+222);

		Order.setfkstatus("��������");
		
		orderDAO.attachDirty(Order);
		this.setMessage("�����ɹ�");
		this.setPath("myOrder.action");
		return "succeed";
	}
	
	public String bank()
	{
		
		Map session= ServletActionContext.getContext().getSession();

		String sql="from TOrder where orderId="+orderId;
		List orderList=orderDAO.getHibernateTemplate().find(sql);
		Map request=(Map)ServletActionContext.getContext().get("request");
		request.put("orderList", orderList);
		
		return ActionSupport.SUCCESS;		
	
	}
	public TGoodsDAO getGoodsDAO()
	{
		return goodsDAO;
	}
	
	public String getOdderFukuangfangshi()
	{
		return odderFukuangfangshi;
	}



	public void setOdderFukuangfangshi(String odderFukuangfangshi)
	{
		this.odderFukuangfangshi = odderFukuangfangshi;
	}



	public String getOdderSonghuodizhi()
	{
		return odderSonghuodizhi;
	}



	public void setOdderSonghuodizhi(String odderSonghuodizhi)
	{
		this.odderSonghuodizhi = odderSonghuodizhi;
	}



	public void setGoodsDAO(TGoodsDAO goodsDAO)
	{
		this.goodsDAO = goodsDAO;
	}
	public int getGoodsId()
	{
		return goodsId;
	}
	
	public int getOrderId()
	{
		return orderId;
	}



	public void setOrderId(int orderId)
	{
		this.orderId = orderId;
	}






	public String getMessage()
	{
		return message;
	}



	public void setMessage(String message)
	{
		this.message = message;
	}



	public String getPath()
	{
		return path;
	}



	public void setPath(String path)
	{
		this.path = path;
	}


	public void setGoodsId(int goodsId)
	{
		this.goodsId = goodsId;
	}
	public TOrderDAO getOrderDAO()
	{
		return orderDAO;
	}
	public void setOrderDAO(TOrderDAO orderDAO)
	{
		this.orderDAO = orderDAO;
	}
	public TOrderItemDAO getOrderItemDAO()
	{
		return orderItemDAO;
	}
	public void setOrderItemDAO(TOrderItemDAO orderItemDAO)
	{
		this.orderItemDAO = orderItemDAO;
	}
	public int getQuantity()
	{
		return quantity;
	}
	public void setQuantity(int quantity)
	{
		this.quantity = quantity;
	}




	

}

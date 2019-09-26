package smyhw;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.command.Command;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;

import org.bukkit.*;

public class awa extends JavaPlugin implements Listener{
	double set_jj=0;//降价
	double set_zj=0;//涨价
	double set_dj=0;//低价
	boolean set_kt=false;//是否统计方块挖掘数量
	boolean set_player_list=false;//是否记录在线玩家列表
	boolean set_chattb=false;//是否记录玩家聊天
	String set_chattb_url="E:\\ctb";//玩家聊天记录位置
	String set_player_list_url="E:\\pl";//玩家在线列表位置
	
	chattb_thread chattb_;//信息转发线程
	@Override
    public void onEnable() {      
        getLogger().info("yh数据统计正在加载");
        Bukkit.getPluginManager().registerEvents(this,this);
        load_set();
        if(set_chattb==true) 
        {
        	chattb_ = new chattb_thread(set_chattb_url+"_QQ");
        	chattb_.start();
        }
        getLogger().info("yh数据统计已经完全加载");
//        saveDefaultConfig();
    }

	@Override
    public void onDisable() {
		if(set_chattb==true) {chattb_.stop();}
        getLogger().info("yh数据统计已经卸载");
    }
    @SuppressWarnings("deprecation")
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
    {
    	//begin_of_yh
    	if (cmd.getName().equalsIgnoreCase("yh"))
    	{
    		if(!(sender.hasPermission("yh.admin"))) {sender.sendMessage("禁止查询！");return true;}
    		if(args.length<1){sender.sendMessage("你要查啥？加参数啊老哥！");return true;}
    		
    		switch(args[0])
    		{
    		//begin_of_set
    		case "set":
    			if(args.length<3){sender.sendMessage("*/yh set <Double/Boolean>(D/B) <条目> <数据>");return true;}
//    			System.out.println(args[0]+","+args[1]+","+args[2]+","+args[3]+",");
    			if(args[1].equals("B"))
    			{
    				if(args[3].equals("true")) {getConfig().set(args[2],true);}
    				else {getConfig().set(args[2],false);}
    			}
    			if(args[1].equals("D"))
    			{
    				getConfig().set(args[2],Double.valueOf(args[3].toString()));
    			}
    			if(args[1].equals("S"))
    			{
    				getConfig().set(args[2],args[3]);
    			}
    			sender.sendMessage("配置文件条目"+args[2]+"已被更改为"+args[3]);
    			saveConfig();
    			return true;
 //   			break;
    		//end_of_set
    		//begin_of_reload
    		case "reload" :
    			load_set();
    	        break;
    	    //end_of_reload
    		//begin_of_矿
    		case "k":
    			if(args.length>=3)
    			{
    				int re = getConfig().getInt(args[1]+".BreakBlock."+args[2]);
    				sender.sendMessage("玩家"+args[1]+"总共破坏了"+re+"个"+args[2]);return true;
    			}else {sender.sendMessage("你要查谁啊？你要查哪个方块啊？倒是写全啊！");return true;}
//    			break;
    			//end_of_矿
    			//begin_of_物价
    		case "wj":
    			if(args.length<2) {sender.sendMessage("缺少参数");return false;}
    			if(args[1].equals("c"))
    			{
    				if(args.length>=3)
    				{
    					int sl = getConfig().getInt(args[2]);
    					sender.sendMessage("玩家共卖出了"+sl+"个"+args[2]);
    					return true;
    				}
    				else
    				{
    					Material wp = ((Player)sender).getInventory().getItemInMainHand().getType();
    					int wpp = wp.getId();
    					int sl = ((Player)sender).getInventory().getItemInMainHand().getAmount();
    					sender.sendMessage("玩家共卖出了"+sl+"个"+wpp);
    					return true;
    				}
    			}
    			
    			if(args[1].equals("set"))
    			{
    				if(args.length<3) {sender.sendMessage("缺少参数");return false;}
    				if(args.length<4)
    				{
    					Material wp = ((Player)sender).getInventory().getItemInMainHand().getType();
    					int wpp = wp.getId();
    					double jg = Double.valueOf(args[2]);
    					getConfig().set("sell.standard."+wpp,jg);
    					sender.sendMessage("物品"+wpp+"的价格设置为"+jg);
    					if(getConfig().getDouble("sell.now."+wpp)==0);
    					{
    						getConfig().set("sell.now."+wpp,jg);
    					}
    					saveConfig();
    					return true;
    				}
    				else
    				{
    					double jg = Double.valueOf(args[3]);
    					getConfig().set("sell.standard."+args[2],jg);
    					sender.sendMessage("物品"+args[2]+"的价格设置为"+jg);
    					if(getConfig().getDouble("sell.now."+args[2])==0);
    					{
    						getConfig().set("sell.now."+args[2],jg);
    					}
    					saveConfig();
    					return true;
    				}
    			}
//    			break;
    		//end_of_物价
    		//bengin of 聊天同步
//    		case "chattb" :
//    			chattb_thread chattb_ = new chattb_thread("//home//smyhw//work_temp//test");
//    			chattb_.run();
    		//end of 聊天同步
    		}
    		//end_switch
    	}
    	//end_of_yh
    	if (cmd.getName().equalsIgnoreCase("test"))
    	{          
    		sender.sendMessage("smyhw test message!");
    		System.out.println("smyhw test message!");
    		return true;                                                       
    	}
            //卖出物品
    	if (cmd.getName().equalsIgnoreCase("selld"))
    	{
    		if
    		(
    			!
    			(
    			(sender.hasPermission
    					("smyhw.inworld.ourworld")
    			) 
    			|| 
    			(sender.hasPermission
    					("smyhw.inworld.rpg")
    			)
    			)
    		)
    		{sender.sendMessage("请在主世界或RPG世界售卖物品");return true;}
    		Material wp = ((Player)sender).getInventory().getItemInMainHand().getType();
    		int wpp = wp.getId();
    		String wppp = "sell.Statistics."+wpp+"";//获取物品统计键值
    		int sl = ((Player)sender).getInventory().getItemInMainHand().getAmount();//获取玩家手上物品的数量
    		int sll = sl+getConfig().getInt(wppp);//将玩家手上物品的数量加到原本的存储的统计数量上
    		String wpppp = "sell.nowjs."+wpp+"";//获取物品的现在计数键值
    		int slll = sl+getConfig().getInt(wpppp);//将玩家手上物品的数量加到原本的存储的现在计数上
    		if(getConfig().getDouble("sell.standard."+wpp)==0)
    		{
    			sender.sendMessage("物品无法被卖出！");
    			return true;
    		}
    		if(!(sender.hasPermission("yh.admin")))
    		{
    			getConfig().set(wppp,sll);
    			getConfig().set(wpppp,slll);
    		}
    		saveConfig();
    		if(sender.isOp())
    		{
    			Bukkit.dispatchCommand(sender,"sell hand "+sl);
    		}
    		else
    		{
    			sender.setOp(true);
    			Bukkit.dispatchCommand(sender,"sell hand "+sl);
    			sender.setOp(false);
    		}
    		//begin降价判断
    		while(true)
    		{
    			if(getConfig().getInt(wpppp)>=64)
    			{
    				getConfig().set(wpppp,(getConfig().getInt(wpppp)-64));
    				if((getConfig().getDouble("sell.now."+wpp)-0.1)>0.1)
    				{
    					getConfig().set("sell.now."+wpp,getConfig().getDouble("sell.now."+wpp)-set_jj);//降价
    				}else {getConfig().set("sell.now."+wpp,set_dj);}
    				Bukkit.dispatchCommand(Bukkit.getConsoleSender(),"setworth "+wpp+" "+getConfig().getDouble("sell.now."+wpp));
//    				sender.sendMessage("setworth "+wpp+" "+getConfig().getInt("sell.now."+wpp));
    				//begin价格回升
    				int ID=0;
    				while(true)
    				{
    					if(ID==500) {break;}//如果物品ID超过500，结束循环
    					if(getConfig().getDouble("sell.standard."+ID)==0) {ID++;continue;}//如果物品没有价格，跳过
    					if(ID==wpp) {ID++;continue;}//如果物品和当前卖出的物品一样,跳过
    					if(getConfig().getDouble("sell.now."+ID)>getConfig().getDouble("sell.standard."+ID)) {ID++;continue;}//如果物品售价大于标准售价，跳过
    					getConfig().set("sell.now."+ID,getConfig().getDouble("sell.now."+ID)+set_zj);//价格回升
        				Bukkit.dispatchCommand(Bukkit.getConsoleSender(),"setworth "+ID+" "+getConfig().getDouble("sell.now."+ID));
    					ID++;
    				}
    				//end价格回升
    			}else {break;}
    		}
    		//end降价判断
    		saveConfig();
    		return true;
    	}
    	return false;
    }
//监视方块破坏
    @EventHandler
    public void ktg(BlockBreakEvent e)
    {
    	if(set_kt==false) {return;}
    	int fkid = e.getBlock().getType().getId();
    	String nc = e.getPlayer().getName();
		getConfig().set(nc+".BreakBlock."+fkid,getConfig().getInt(nc+".BreakBlock."+fkid)+1);
		saveConfig();
    }
    @EventHandler
    public void ktg(PlayerJoinEvent e) throws Exception
    {
    	if(set_player_list==false) {return;}
//    	System.out.println("gg");
    	String temp1 = e.getPlayer().getName();
    	getLogger().info("玩家"+temp1+"加入，写入文件！");
    	player_online_list_file(temp1,1);
    }
    @EventHandler
    public void ktg(PlayerQuitEvent e) throws Exception
    {
    	if(set_player_list==false) {return;}
//    	System.out.println("aa");
    	String temp1 = e.getPlayer().getName();
    	getLogger().info("玩家"+temp1+"退出，删改文件！");
    	player_online_list_file(temp1,0);
    }
    //将玩家信息读取或写入
    //type==1:添加玩家
    //type==0:删除玩家
    void player_online_list_file(String name,int type) throws Exception
    {
    	if(type==1)
    	{
//        	BufferedWriter output = new BufferedWriter(new FileWriter("E:\\pl"));
//
//    		output.newLine();
//    		output.write(name);
//    		output.close();
    	      FileWriter writer = new FileWriter(set_player_list_url, true);
    	      writer.write(name+"\r\n");
    	      writer.close();
    	}
    	else
    	{
    		File temp = new File(set_player_list_url+".temp");
    		temp.createNewFile();
        	BufferedReader input = new BufferedReader(new FileReader(set_player_list_url));
        	BufferedWriter tempfile = new BufferedWriter(new FileWriter(set_player_list_url+".temp"));
    		while(true)
    		{
    			String temp1=input.readLine();//temp1:每次从文件中读出的一行
    			if(temp1==null) {break;}
    			if(temp1.equals(name)){continue;}
    			tempfile.write(temp1);
    			tempfile.newLine();
    		}
    		input.close();
    		tempfile.close();
    		File temp2 = new File(set_player_list_url);
    		Boolean temp3;
    		temp3=temp2.delete();
    		while(temp3==false)
    		{
            	getLogger().info("删除源文件失败,正在重试...");
    			temp3=temp2.delete();
    		}
    		temp3=temp.renameTo(new File(set_player_list_url));
    		while(temp3==false)
    		{
    			getLogger().info("更名文件失败,正在重试...");
    			temp3=temp.renameTo(new File(set_player_list_url));
    		}
    	}
    }
    //监听聊天信息并写入文件
    @EventHandler
    public void chattb(AsyncPlayerChatEvent e) throws Exception
    {
    	File chattb_file = new File(set_chattb_url);
    	if(chattb_file.exists()){}else{chattb_file.createNewFile();}
		FileWriter writer = new FileWriter(chattb_file, true);
		writer.write(e.getPlayer().getName()+":"+e.getMessage()+"\r\n");
		writer.close();
    }
    //监听玩家死亡并传送至QQ
    @EventHandler
    public void chattb(PlayerDeathEvent e) throws Exception
    {
    	File chattb_file = new File(set_chattb_url);
    	if(chattb_file.exists()){}else{chattb_file.createNewFile();}
		FileWriter writer = new FileWriter(chattb_file, true);
		writer.write("[死亡信息]"+e.getEntity().getName()+"回到了重生点"+"\r\n");
		writer.close();
    }
    //=========================写一堆lib在最后XD===========
    public void load_set()
    {
        getLogger().info("yh数据统计：正在加载配置...");
        set_zj=getConfig().getDouble("config.zj");
        set_jj=getConfig().getDouble("config.jj");
        set_dj=getConfig().getDouble("config.dj");
        set_kt=getConfig().getBoolean("config.kt");
        set_player_list=getConfig().getBoolean("config.player_list");
        set_chattb=getConfig().getBoolean("config.chattb");
        set_player_list_url=getConfig().getString("config.player_list_url");
        set_chattb_url=getConfig().getString("config.chattb_url");
        getLogger().info("config.zj:"+set_zj);
        getLogger().info("config.jj:"+set_jj);
        getLogger().info("config.dj:"+set_dj);
        getLogger().info("config.kt:"+set_kt);
        getLogger().info("config.player_list:"+set_player_list);
        getLogger().info("config.player_list_url:"+set_player_list_url);
        getLogger().info("config.chattb:"+set_chattb);
        getLogger().info("config.chattb_url:"+set_chattb_url);
        getLogger().info("yh数据统计：完成配置加载...");
    }
    //=========================真就lib呗...===============
}
//用于消息同步。。。
class chattb_thread extends Thread
{
	String file_URL;
	public chattb_thread(String file_URL_input)
	{
		file_URL=file_URL_input;
	}
	public void run()
	{
		while(true)
		try {
			File file = new File(file_URL);
			String text;
			/*处理CQ码时所用，已报废
			char cl[];
			int xh=0,xh1=0;
			String CQtext="_CQtext_error_";
			String q="_q_error_",h="_h_error_";
			int a_QQb=0,a_QQe=0;
			String a_QQ="_a_QQ_errer_";
			*/
			while(true)
			{
				if(file.exists())
				{
					BufferedReader input = new BufferedReader(new FileReader(file));
					while(true)
					{
//						System.out.println("gg");
						text=input.readLine();
						if(text==null) {break;}
/*
						//处理文本（例如过滤CQ码）
						xh=0;xh1=1;
						CQtext="_CQtext_error_";q="_q_error_";h="_h_error_";
						while(true)
						{
							if(xh==(text.length()-1)) {break;}
							if
							(
									(text.charAt(xh)=='[') &&
									(text.charAt(xh+1)=='C') &&
									(text.charAt(xh+2)=='Q') &&
									(text.charAt(xh+3)==':') 
							)
							{//探测到CQ码，检测类型
								//at类型
								if(text.charAt(xh+4)=='a')
								{
//									System.out.println("aa");
									while(true)//遍历文本，找到‘=’
									{
										if((text.charAt(xh1))=='=') //找到QQ号开始符
										{
											a_QQb=(xh1+1);
											break;
										}
										if(xh1==(text.length()-1)) {a_QQb=(text.length()-1);break;}//如果找不到，就定义到末尾
										xh1++;
									}
									while(true)//遍历文本，找到‘]’
									{
										if((text.charAt(xh1))==']') //找到CQ结束符，记录QQ号结束
										{
											a_QQe=(xh1);
											break;
										}
										if(xh1==(text.length()-1)) {a_QQe=(text.length()-1);break;}//如果找不到，就定义到末尾
										xh1++;
									}
									a_QQ=text.substring(a_QQb, a_QQe);
									//QQ号转昵称
									BufferedReader db_input = new BufferedReader(new FileReader("E://QNL"));
					        		String temp=db_input.readLine();
					        		while(true)
					        		{
//					        			System.out.println(a_QQ+"?"+temp);
					        			if(temp==null) {temp=(a_QQ+"");break;}
					        			if(temp.equals(a_QQ+"")) 
					        			{
					        				temp=db_input.readLine();
					        				break;
					        			}
					            		temp=db_input.readLine();
					            		temp=db_input.readLine();
					        		}
					        		db_input.close();
									CQtext=("@"+temp);
								}
								xh1=1;
								//图片类型
								if(text.charAt(xh+4)=='i') {CQtext="[图片]";}
								xh1=1;
								//表情类型
								if(text.charAt(xh+4)=='f') {CQtext="[表情]";}
								xh1=1;
								q=text.substring(0,xh);//剥离出前文本
								xh1=xh+4;
								while(true)//遍历文本，找到‘]’
								{
									if((text.charAt(xh1))==']') //找到CQ结束符，剥离后文本
									{
										h=text.substring(xh1+1);
										break;
									}
									if(xh1==(text.length()-1)) {break;}
									xh1++;
								}
								//接合前后文本，并在中间加入替换CQ的文本
								text=(q+CQtext+h);
						}
							xh++;
						}
						//结束文本处理
*/
						Bukkit.broadcastMessage("§a[QQ]:§b"+text);
					}
					input.close();
					if(file.delete()==false)
					{
						while(true)
						{
							if(file.delete()==true) {break;}
							System.out.println("删除聊天同步文件出错！反复尝试中！（线程阻塞）");
							Thread.sleep(3000);
						}
					};
				}
				Thread.sleep(3000);
//				System.out.println("aaa");
			}
		}catch (Exception e) {continue;}
	}
}
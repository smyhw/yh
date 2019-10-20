package smyhw;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
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

import me.clip.placeholderapi.PlaceholderAPI;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;

import org.bukkit.*;

public class awa extends JavaPlugin implements Listener{
	double set_jj=0;//降价
	double set_zj=0;//涨价
	double set_dj=0;//低价
	boolean set_kt=false;//是否统计方块挖掘数量
//	boolean set_player_list=false;//是否记录在线玩家列表
	boolean set_chattb=false;//是否记录玩家聊天
//	String set_chattb_url="E:\\ctb";//玩家聊天记录位置
//	String set_player_list_url="E:\\pl";//玩家在线列表位置
	boolean set_PlayerDeath=false;//是否统计玩家死亡次数
	
	chattb_thread chattb_;//信息转发线程
	dt_thread dt_thread_;//操作线程
	@Override
    public void onEnable() {      
        getLogger().info("yh数据统计正在加载");
        Bukkit.getPluginManager().registerEvents(this,this);
        load_set();//载入配置文件
        if(set_chattb==true) 
        {
        	chattb_ = new chattb_thread(getLogger());
        	chattb_.start();
        }

        //数据对接进程
        dt_thread_ = new dt_thread(getLogger(),getConfig(),Bukkit.getOnlinePlayers());
        dt_thread_.start();
        getLogger().info("yh数据统计已经完全加载");
//        saveDefaultConfig();
    }

	@Override
    public void onDisable() {
		if(set_chattb==true) 
		{
			chattb_.gg();
			chattb_.stop();
		}
		
		//关闭数据对接进程
        dt_thread_ .gg();
        dt_thread_.stop();
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
    //用于将玩家列表计入文本，功能已过时，除去！
//    @EventHandler
//    public void ktg(PlayerJoinEvent e) throws Exception
//    {
//    	if(set_player_list==false) {return;}
////    	System.out.println("gg");
//    	String temp1 = e.getPlayer().getName();
//    	getLogger().info("玩家"+temp1+"加入，写入文件！");
//    	player_online_list_file(temp1,1);
//    }
//    @EventHandler
//    public void ktg(PlayerQuitEvent e) throws Exception
//    {
//    	if(set_player_list==false) {return;}
////    	System.out.println("aa");
//    	String temp1 = e.getPlayer().getName();
//    	getLogger().info("玩家"+temp1+"退出，删改文件！");
//    	player_online_list_file(temp1,0);
//    }
//    //将玩家信息读取或写入
//    //type==1:添加玩家
//    //type==0:删除玩家
//    void player_online_list_file(String name,int type) throws Exception
//    {
//    	if(type==1)
//    	{
////        	BufferedWriter output = new BufferedWriter(new FileWriter("E:\\pl"));
////
////    		output.newLine();
////    		output.write(name);
////    		output.close();
//    	      FileWriter writer = new FileWriter(set_player_list_url, true);
//    	      writer.write(name+"\r\n");
//    	      writer.close();
//    	}
//    	else
//    	{
//    		File temp = new File(set_player_list_url+".temp");
//    		temp.createNewFile();
//        	BufferedReader input = new BufferedReader(new FileReader(set_player_list_url));
//        	BufferedWriter tempfile = new BufferedWriter(new FileWriter(set_player_list_url+".temp"));
//    		while(true)
//    		{
//    			String temp1=input.readLine();//temp1:每次从文件中读出的一行
//    			if(temp1==null) {break;}
//    			if(temp1.equals(name)){continue;}
//    			tempfile.write(temp1);
//    			tempfile.newLine();
//    		}
//    		input.close();
//    		tempfile.close();
//    		File temp2 = new File(set_player_list_url);
//    		Boolean temp3;
//    		temp3=temp2.delete();
//    		while(temp3==false)
//    		{
//            	getLogger().info("删除源文件失败,正在重试...");
//    			temp3=temp2.delete();
//    		}
//    		temp3=temp.renameTo(new File(set_player_list_url));
//    		while(temp3==false)
//    		{
//    			getLogger().info("更名文件失败,正在重试...");
//    			temp3=temp.renameTo(new File(set_player_list_url));
//    		}
//    	}
//    }
    //监听聊天信息并写入文件
    @EventHandler
    public void chattb(AsyncPlayerChatEvent e) throws Exception
    {
    	new chattb_sender(chattb_.s,getLogger(),(e.getPlayer().getName()+":"+e.getMessage())).start();
    }
    //监听玩家死亡并传送至QQ
    @EventHandler
    public void chattb(PlayerDeathEvent e) throws Exception
    {
    	new chattb_sender(chattb_.s,getLogger(),("[死亡信息]"+e.getEntity().getName()+"回到了重生点")).start();
    }
    
    //监听玩家死亡并计入配置文件
    @EventHandler
    public void PlayerDeath(PlayerDeathEvent e) throws Exception
    {

    	if(set_PlayerDeath==false) {return;}//如果设置里没有启动这个功能，则直接结束
    	String name="PlayerDeath."+e.getEntity().getName();//获取配置文件字段
    	int temp=getConfig().getInt(name);//获取原数据
    	temp=temp+1;//原数据加上这次的死亡
    	getConfig().set(name,temp);//写回配置文件
    	saveConfig();
    }
    //=========================写一堆lib在最后XD===========
    public void load_set()
    {
        getLogger().info("yh数据统计：正在加载配置...");
        set_zj=getConfig().getDouble("config.zj");//
        set_jj=getConfig().getDouble("config.jj");//
        set_dj=getConfig().getDouble("config.dj");//
        set_kt=getConfig().getBoolean("config.kt");//是否记录玩家挖掘方块数量
//        set_player_list=getConfig().getBoolean("config.player_list");
        set_chattb=getConfig().getBoolean("config.chattb");//是否开启聊天转发
//        set_player_list_url=getConfig().getString("config.player_list_url");
//        set_chattb_url=getConfig().getString("config.chattb_url");
        set_PlayerDeath=getConfig().getBoolean("config.PlayerDeath");//是否记录玩家死亡次数
        getLogger().info("config.zj:"+set_zj);
        getLogger().info("config.jj:"+set_jj);
        getLogger().info("config.dj:"+set_dj);
        getLogger().info("config.kt:"+set_kt);
//        getLogger().info("config.player_list:"+set_player_list);
//        getLogger().info("config.player_list_url:"+set_player_list_url);
        getLogger().info("config.chattb:"+set_chattb);
//        getLogger().info("config.chattb_url:"+set_chattb_url);
        getLogger().info("config.PlayerDeath:"+set_PlayerDeath);
        getLogger().info("yh数据统计：完成配置加载...");
    }
    //=========================真就lib呗...===============
}
//用于消息同步。。。
class chattb_thread extends Thread
{
	ServerSocket ss;
	public Socket s;
	Logger l;
	chattb_accept cc;
	DataOutputStream out;
	public chattb_thread(Logger l_input)
	{
		l=l_input;
	}
	public void run()
	{
		try {ss = new ServerSocket(4201);} catch (IOException e) {l.info("[聊天信息转发]：错误，监听端口失败！正在尝试结束线程以阻止可能的严重错误发生");return;};
		while(true)
		{
			l.info("开始等待新连接...");
			try {s = ss.accept();} catch (IOException e) {l.info("[聊天信息转发]：警告，一个连接接受失败，抛弃该连接！");continue;}
			l.info("成功建立一个连接");
			cc = new chattb_accept(s,l);
			cc.start();
			try {out = new DataOutputStream(s.getOutputStream());} catch (IOException e) {}
			while(true)
			{
				try {out.writeUTF("#xt");} catch (IOException e) {l.info("心跳包发送错误！");break;}
//				System.out.println("发送一个心跳包");
				try {Thread.sleep(30000);} catch (InterruptedException e) {l.info("延时错误...（这是尼玛什么鬼错误...）");break;}
			}
		}
	}
	public void gg()
	{
		try {cc.stop();} catch (Exception e) {System.out.println("gg is error!(1)");}
		try {s.close();} catch (Exception e) {System.out.println("gg is error!(2)");}
		try {ss.close();} catch (Exception e) {System.out.println("gg is error!(3)");}
		
	}
}

//用于接受信息
class chattb_accept extends Thread
{
	Socket s;
	Logger l;
	String input_msg;
	public chattb_accept(Socket s,Logger l)
	{
		this.s=s;
		this.l=l;
	}
	public void run()
	{
		try {
			DataInputStream in = new DataInputStream(s.getInputStream());
//			DataOutputStream out = new DataOutputStream(s.getOutputStream());
			s.setSoTimeout(60000);
			while(true)
			{
				try{input_msg=in.readUTF();}catch(Exception e) {l.warning("警告，客户端心跳丢失！丢弃连接！");return;}
//				System.out.println(input_msg);
				if(!input_msg.equals("#xt"))//确认是心跳包则不处理，不是则发送
				{
					Bukkit.broadcastMessage("§a[QQ]:§b"+input_msg);
				}
			}
		} catch (IOException e) {
			l.warning("警告，接收信息时发生错误！丢弃连接！");
			return;
		}
	}
}

//用于发送消息
class chattb_sender extends Thread
{
	Socket s;
	Logger loger;
	DataOutputStream out;
	String msg;
	public chattb_sender(Socket s,Logger l,String msg)
	{
		this.s=s;
		this.loger=l;
		this.msg=msg;
	}
	public void run()
	{
		try {
			out = new DataOutputStream(s.getOutputStream());
		} 
		catch (Exception e) 
		{
			loger.warning("发送信息时获取输出流错误！取消本次发送");
			return;
		}
		try {out.writeUTF(msg);return;} catch (Exception e) {loger.warning("发送信息失败！取消发送！");return;}
	}
}

//数据对接线程
class dt_thread extends Thread
{
	Collection<? extends Player> Players;
	String temp;
	static ServerSocket ss;
	Socket s;
	DataOutputStream out;
	DataInputStream in;
	Logger loger;
	FileConfiguration configer;
	public dt_thread(Logger l,FileConfiguration configer,Collection<? extends Player> Players)
	{
		this.Players=Players;
		this.loger = l;
		this.configer=configer;
		try {ss = new ServerSocket(4202);} catch (IOException e) {loger.warning("pl监听失败");}
		loger.info("pl监听开始");
	}
	public void run()
	{
		while(true)
		{
			try {s=ss.accept();} catch (Exception e) {loger.warning("操作信道连接失败");continue;}
			new dts_thread(loger,configer,s,Players).run();
			loger.info("操作信道连接完毕");
			//这里开一个新线程去处理这个连接，这线程继续等待下一个连接
//			try {out = new DataOutputStream(s.getOutputStream());} catch (Exception e) {System.out.println("操作信道流打开失败");continue;}
//			try {in = new DataInputStream(s.getInputStream());} catch (Exception e) {System.out.println("操作信道流打开失败");continue;}
//			try {temp=in.readUTF();} catch (Exception e) {System.out.println("操作信道读取操作指令失败");continue;}
//			if(temp.equals("#pl"))
//			{
//				for(Player p :Players)
//				{
//					temp=p.getName();
//					try {out.writeUTF(temp);} catch (Exception e) {System.out.println("pl写失败");continue;}
//				}
//				continue;//处理完这指令，不要继续判断
//			}
//			try {s.close();} catch (Exception e) {System.out.println("pl未知错误");continue;}
		}
	}
	public static void gg()
	{
		try {
			ss.close();
		} catch (Exception e) {
			System.out.println("pl_gg_error");
		}
	}
}

//用于处理一个dt连接
class dts_thread extends Thread
{
	Logger loger;
	FileConfiguration configer;
	Collection<? extends Player> Players;
	String temp;
	Socket s;
	DataOutputStream out;
	DataInputStream in;
	public dts_thread(Logger l,FileConfiguration configer,Socket s,Collection<? extends Player> Players)
	{
		this.s=s;
		this.Players=Players;
		this.loger=l;
		this.configer =configer;
	}
	public void run()
	{
		try {out = new DataOutputStream(s.getOutputStream());} catch (Exception e) {loger.warning("操作信道流打开失败");return;}
		try {in = new DataInputStream(s.getInputStream());} catch (Exception e) {loger.warning("操作信道流打开失败");return;}
		try {temp=in.readUTF();} catch (Exception e) {loger.warning("操作信道读取操作指令失败");return;}
		
		//功能：获取玩家列表
		if(temp.equals("#pl"))
		{
			for(Player p :Players)
			{
				temp=p.getName();
				try {out.writeUTF(temp);} catch (Exception e) {loger.warning("pl写失败");continue;}
			}
			try {s.close();} catch (Exception e) {loger.warning("操作信道关闭错误！");return;}
			return;//处理完这指令，不要继续判断
		}
		
		//功能：执行指令
		if(temp.equals("#command"))
		{
//			return;//注意！这个功能十分危险！禁用他！
//			try {temp=in.readUTF();} catch (IOException e) {}
//			Bukkit.dispatchCommand(Bukkit.getConsoleSender(),temp);
			try {s.close();} catch (Exception e) {loger.warning("操作信道关闭错误！");return;}
			return;
		}
		
		//功能：获取玩家死亡计数
		if(temp.equals("#death"))
		{
			try {temp=in.readUTF();} catch (IOException e1) {loger.warning("查询玩家死亡次数，获取查询模式失败！");try {in.close();} catch (IOException e) {}return;}//获取查询详情。。。
			if(temp.equals("p"))
			{
				try {temp=in.readUTF();} catch (IOException e1) {loger.warning("获取查询类别错误！");}//获取查的谁
				temp=configer.getInt("PlayerDeath."+temp)+"";
				try {out.writeUTF(temp);} catch (IOException e) {loger.warning("玩家死亡计数发送错误！");}
			}
			if(temp.equals("l"))
			{
				int sn=0;
				int t=0;
				int db=0;
				File ej = new File("./plugins/yh/config.yml");
				int []arr= new int[0];
				String []arrn=new String[0];
				String []temps=new String[10];
	        	try 
	        	{
	        		//探测有多少玩家数据。。。
	        		
					BufferedReader input = new BufferedReader(new FileReader(ej));
//					if(db==0)return;
//					BufferedWriter output = new BufferedWriter(new FileWriter(ej));
//					System.out.println("aa");
					while(true)
					{
						temp=input.readLine();
//						System.out.println("a"+temp);
						if(temp==null) {break;}
						temp=temp.trim();
						if(t==1)
						{
							if(temp.equals("zzz:")) {break;}
							sn++;
						}
						if(temp.equals("PlayerDeath:")){t=1;}//找到内容
					}
//					System.out.println("bb");
					input.close();
//					output.close();
	        		//========================
					arr = new int[sn];
					arrn = new String[sn];
					sn=0;t=0;
					input = new BufferedReader(new FileReader(ej));
//					output = new BufferedWriter(new FileWriter(ej));
//					System.out.println("cc");
					while(true)
					{
						temp=input.readLine();
//						System.out.println("c"+temp);
						if(temp==null) {break;}
						temp=temp.trim();
						if(t==1)
						{
							if(temp.equals("zzz:")) {break;}
							temps=temp.split(":");
							temp=temps[1];
							temp=temp.trim();
//							System.out.println("ccc"+temp);
							arr[sn]=Integer.parseInt(temp);
							arrn[sn]=temps[0];
							sn++;
						}
						if(temp.equals("PlayerDeath:")){t=1;}//找到内容
					}
					input.close();
				} catch (Exception e1) {loger.warning("玩家死亡列表输入输出流异常");}
				
				//========================
				for (int i = 0; i < arr.length; i++) 
				{
//					System.out.println("aa"+arr[i]);
					for (int j = i + 1; j < arr.length; j++) 
					{
						if (arr[i] < arr[j]) 
						{
							int tmp = (int)arr[i];
							arr[i] = arr[j];
							arr[j] = tmp;
							
							String tmp1 = arrn[i];
							arrn[i]=arrn[j];
							arrn[j]=tmp1;
						}
					}
				}
				//=======================
				try 
				{
					for(int i=0;i<10;i++)
					{
						out.writeUTF(arrn[i]+"——"+arr[i]);
//						System.out.println(i+"gg"+(int)arr[i]);
					}
				} 
				catch (Exception e) 
				{loger.warning(e.getMessage());loger.warning("玩家死亡排行榜发送错误！");}
				//========================================
			}

			try {s.close();} catch (Exception e) {loger.warning("操作信道关闭错误！");return;}
			return;//处理完这指令，不要继续判断
		}
		
		//获取服务器状态
		if(temp.equals("#st"))
		{
			try
			{
				out.writeUTF("mc.smyhw.online:25565");
				out.writeUTF("在线");
				out.writeUTF("9/"+Players.size());
				String TPS = new String("%server_tps_1%");
				TPS = PlaceholderAPI.setPlaceholders(null,TPS);
				out.writeUTF(TPS);
			}
			catch(Exception e)
			{
				loger.warning(e.getMessage());loger.warning("服务器信息发送错误");
			}
			try {s.close();} catch (Exception e) {loger.warning("操作信道关闭错误！");return;}
			return;
		}
		//
		try {s.close();} catch (Exception e) {loger.warning("操作信道关闭错误！");return;}
	}
}

package com.archy.dezhou.backlet;

/**
 *@author archy_yu 
 **/

import com.archy.dezhou.entity.Player;
import com.archy.dezhou.entity.User;
import com.archy.dezhou.backlet.base.DataBacklet;
import com.archy.dezhou.container.ActionscriptObject;
import com.archy.dezhou.container.SFSObjectSerializer;
import com.archy.dezhou.global.UserModule;
import com.archy.dezhou.global.PlayerService;
import io.netty.handler.codec.http.FullHttpResponse;

import java.util.HashMap;
import java.util.Map;

public class PlayerManageBacklet extends DataBacklet
{
	
	private final static String USERLOGIN = "userlogin";
	private final static String REGISTER = "register";
	private final static String REGISTERUPDATE = "registerupdate";
	private final static String PASSWORDUPDATE = "passwordupdate";
	private final static String UINFO = "uinfo";
	private final static String LOGOUT = "logout";
	private final static String PICUPDATE = "picUpdate";
	private final static String RUSHACH = "rach";
	@Override
	public byte[] process(String subCmd, Map<String, String> parms,FullHttpResponse response)
	{
		byte[] xmlByteA = null;
		String XmlError = "";
		String XmlOk = "";
		
		if(subCmd.equals(USERLOGIN))
		{
			String userName = parms.get("name");
			String userPassword = parms.get("password");
			String userid = parms.get("userid") ;
			String key = parms.get("key");
			
			response.headers().set("cmd", "userlogin");
			response.headers().set("ts", "-1");
			response.headers().set("num", "0");
			if (userName.equals("") || userName == null
					|| userPassword.equals("")
					|| userPassword == null)
			{
				XmlError = BackletKit.errorXml("NameOrPasswordIdNull");
				xmlByteA = XmlError.getBytes();
			}
			else
			{
				xmlByteA = PlayerService.UserLogin(userName,
						userPassword, false,
						userid, key, 0, false);
				xmlByteA = BackletKit.SimpleObjectXml(xmlByteA);
			}
		}
		else if(subCmd.equals(REGISTER))
		{
			 // 用户新注册
			// 请求数据的格式为
			// 用户名-密码-电子邮箱-性别-生日
			String auto = parms.get("auto");
			String userid = parms.get("userid");
			String key = parms.get("key");

			String uid = userid;
			if (userid.length() > 0 && !uid.equals("-1"))
			{
				XmlError = BackletKit.errorXml("UserHasRegistered");
				xmlByteA = XmlError.getBytes();
			}
			if (auto.equals("yes"))
			{
				HashMap<String, String> userinfoList = PlayerService
						.AutoRegister(userid, key);
				if (userinfoList != null
						&& userinfoList.get("name") != null
						&& userinfoList.get("password") != null)
				{
					response.headers().set("cmd", "autoregister");
					response.headers().set("ts", "-1");
					response.headers().set("num", "0");
					xmlByteA = PlayerService.UserLogin( userinfoList.get("name"),
							userinfoList.get("password"),true,
							userid, key, 0, false);
					
					xmlByteA = BackletKit.SimpleObjectXml(xmlByteA);
				}
				else if (userid != null
						&& !userid.trim().equals(""))
				{
					xmlByteA = PlayerService.UserLogin("", "", false,
							 userid, key, 1,
							false);
					xmlByteA = BackletKit.SimpleObjectXml(xmlByteA);
				}
				else
				{
					XmlError = BackletKit.errorXml("AutoRigesterFailed");
					xmlByteA = XmlError.getBytes();
				}
			}
			else
			{
				String userName = parms.get("name");
				String password = parms.get("password");
				String email = parms.get("email");
				String gendar = parms.get("gendar");
				String birthday = parms.get("birthday");
				
				response.headers().set("cmd", "register");
				response.headers().set("ts", "-1");
				response.headers().set("num", "0");
				xmlByteA = PlayerService.Register(userName, password, email, gendar, birthday, userid, key);
			}
		}
		else if(subCmd.equals(REGISTERUPDATE))
		{

			String uid = parms.get("uid") == null ? "" : parms
					.get("uid");
			String email = parms.get("email") == null ? ""
					: parms.get("email");
			String birthday = parms.get("birthday") == null ? ""
					: parms.get("birthday");
			String gendar = parms.get("gendar") == null ? ""
					: parms.get("gendar");
			String name = parms.get("name") == null ? ""
					: parms.get("name");
			String address = parms.get("address") == null ? ""
					: parms.get("address");
			String mobile = parms.get("mobile") == null ? ""
					: parms.get("mobile");
			String newPassword = parms.get("np") == null ? ""
					: parms.get("np");
			String oldPassword = parms.get("op") == null ? ""
					: parms.get("op");
			User uinfo = PlayerService.selectPlayerById(Integer.parseInt(uid));

			response.headers().set("cmd", "registerupdate");
			response.headers().set("ts", "-1");
			response.headers().set("num", "0");
			if ( UserModule.getInstance().getUserByUserId(Integer.parseInt(uid)) == null )
			{
				XmlError = BackletKit.errorXml("UserNotLogined");
				xmlByteA = XmlError.getBytes();
			}
			else if (PlayerService.ifRegistered(name, ""))
			{
				XmlError = BackletKit.errorXml("userNameIsRepeat");
				xmlByteA = XmlError.getBytes();
			}
			else
			{

				if (!email.equals("") && email != null)
					uinfo.setEmail(email);
				if (!birthday.equals("") && birthday != null)
					uinfo.setBirthday(birthday);
				if (!gendar.equals("") && gendar != null)
					uinfo.setGendar(gendar);
				if (!name.equals("") && name != null)
					uinfo.setAccount(name);
				if (!address.equals("") && address != null)
					uinfo.setAddress(address);
				if (!mobile.equals("") && mobile != null)
					uinfo.setMobile(mobile);

				ActionscriptObject PassWordInfo = new ActionscriptObject();
				if (!newPassword.equals("")
						&& !oldPassword.equals(""))
				{
					PassWordInfo.put("op", oldPassword);
					PassWordInfo.put("np", newPassword);
				}
				
				ActionscriptObject upodatetatus = PlayerService.UpdateUserInfo(uinfo, PassWordInfo);
				ActionscriptObject asResponse = null;

				asResponse = PlayerService.getUinfo(uinfo, true);

				if (upodatetatus == null)
				{
					asResponse.put("status", "registerUpdatefail");
					asResponse.put("code", "0");
					asResponse.put("cnt", "用户资料修改失败！");
				}
				else
				{
					asResponse.put("status",
							upodatetatus.get("status"));
					asResponse.put("code",
							upodatetatus.get("code"));
					asResponse.put("cnt", upodatetatus.get("cnt"));
				}
				StringBuffer sb = new StringBuffer();
				sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
				xmlByteA = SFSObjectSerializer.obj2xml(asResponse, 0, "", sb);
				xmlByteA = BackletKit.SimpleObjectXml(xmlByteA);
			}
		}
		else if(subCmd.equals(PASSWORDUPDATE))
		{
			String password = parms.get("password");
			String email = parms.get("email");
			response.headers().set("cmd", "passwordupdate");
			response.headers().set("ts", "-1");
			response.headers().set("num", "0");
			String[] userID =
			{ "uid", "name" };
			if (password.equals("") || email.equals(""))
			{
				xmlByteA = BackletKit.errorXml("parmsInInvalid")
						.getBytes();
			}
			else if (!PlayerService.isvalidEmail(email, userID))
			{
				xmlByteA = BackletKit.errorXml("EmailIsinValid")
						.getBytes();
			}
			else
			{

				xmlByteA = BackletKit.infoXml("Dealing").getBytes();
			}
		
		}
		else if(subCmd.equals(UINFO))
		{
			String uid = parms.get("uid");
			String cuid = parms.get("cuid");

			if (uid.equals("") || cuid.equals(""))
			{
				xmlByteA = BackletKit.errorXml("ParmsIsInvalid")
						.getBytes();
			}
			else
			{
				Player uinfo = UserModule.getInstance().getUserByUserId(Integer.parseInt(uid));
				Player cuinfo = UserModule.getInstance().getUserByUserId(Integer.parseInt(cuid));
				if (uinfo == null)
				{
					xmlByteA = BackletKit.errorXml("YouAreNotLogined!")
							.getBytes();
				}
				else if (cuinfo == null)
				{
					xmlByteA = BackletKit.errorXml("HeIsNotLogined!!")
							.getBytes();
				}
				else
				{
					ActionscriptObject asResponse = null;
					if (uid.equals(cuid)) // edited by 2014-8-2
					{
						asResponse = PlayerService.getUinfo(uinfo, true);
					}
					else
					{
						asResponse = PlayerService.getUinfo(cuinfo, false);
					}
					StringBuffer sb = new StringBuffer();
					sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
					xmlByteA = SFSObjectSerializer.obj2xml(asResponse, 0, "", sb);
					xmlByteA = BackletKit.SimpleObjectXml(xmlByteA);
				}
			}
		
		}
		else if(subCmd.equals(LOGOUT))
		{
			String uid = parms.get("uid");
			Player uinfo = UserModule.getInstance().getUserByUserId(Integer.parseInt(uid));
			if (uinfo != null)
			{
				xmlByteA = BackletKit.infoXml("loginoutOk").getBytes();
			}
			else
			{
				xmlByteA = BackletKit.infoXml("HasLoginOutBefore")
						.getBytes();
			}
		}
		else if(subCmd.equals(RUSHACH))
		{
			String uid = parms.get("uid");
			String rName = parms.get("rn");

		}
		else
		{
			xmlByteA = BackletKit.infoXml("UserManagerParmsIsValid").getBytes();
		}
		return xmlByteA ;
	}
}

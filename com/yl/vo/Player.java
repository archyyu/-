package com.yl.vo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.logging.Logger;

import com.yl.Global.ConstList;
import com.yl.Global.ConstList.PlayerCareerState;
import com.yl.Global.ConstList.PlayerGameState;
import com.yl.Global.UserInfoMemoryCache;
import com.yl.container.ActionscriptObject;
import com.yl.puker.FivePukeItem;
import com.yl.puker.PukerKit;
import com.yl.util.Config;

public class Player
{

	private int seatId = -1; // 座位号
	
	private String userId; // 用户Id
	
	private int yourTurn = 0; // 下注标识 1 代表 yes 0 no

	private PlayerCareerState playerState = PlayerCareerState.PLAYER_STATE_PLAYER;

	private PlayerGameState gameState = PlayerGameState.PLAYER_STATE_PLAYER;

	private int tempBet = 0; // 存放用户面前的临时筹码
	
	private int tempGold = 0;
	
	private int totalGambleBet = 0;
	
	protected Logger log = Logger.getLogger(Config.class.getName());
	
	private Map<Integer,Integer> betMap = new HashMap<Integer,Integer>();
	
	private volatile int dropCardNum = 0;
	
	public Player()
	{
		
	}

	public int getTempBet()
	{
		return tempBet;
	}
	
	public void addTempBet(int bet)
	{
		this.tempBet += bet;
	}
	
	public void clearTempBet()
	{
		this.tempBet = 0;
	}
	
	public void deductTempBet(int bet)
	{
		if(bet <= this.tempBet)
		{
			this.tempBet -= bet;
		}
	}
	
	
	public int getTotalGambleBet()
	{
		return this.totalGambleBet;
	}
	
	public void addTotalGambleBet(int bet)
	{
		if(bet > 0)
		{
			this.totalGambleBet += bet;
		}
	}
	
	public void deductGambleBet(int bet)
	{
		this.totalGambleBet -= bet;
		if(this.totalGambleBet < 0)
		{
			this.totalGambleBet = 0;
		}
	}	
	
	
	public void clearTotalGambleBet()
	{
		this.totalGambleBet = 0;
	}

	public int getYourTurn()
	{
		return yourTurn;
	}

	public void setYourTurn(int yourTurn)
	{
		this.yourTurn = yourTurn;
	}

	public int getSeatId()
	{
		return seatId;
	}

	public void setSeatId(int seatId)
	{
		this.seatId = seatId;
	}

	public String getUserId()
	{
		return userId;
	}

	public void setUserId(String userId)
	{
		this.userId = userId;
	}

	public PlayerCareerState getPlayerState()
	{
		return playerState;
	}

	public void setPlayerState(PlayerCareerState playerState)
	{
		this.playerState = playerState;
	}

	public PlayerGameState getGameState()
	{
		return gameState;
	}

	public void setGameState(PlayerGameState gameState)
	{
		this.gameState = gameState;
	}

	public int getTempGold()
	{
		return tempGold;
	}

	public void setTempGold(int tempGold)
	{
		this.tempGold = tempGold;
	}
	
	private long yourTurnTimeStramp = 0;
	
	public long getYourTurnTimeStramp()
	{
		return this.yourTurnTimeStramp;
	}
	
	public void updateYourTurnTimeStramp()
	{
		this.yourTurnTimeStramp = System.currentTimeMillis();
	}
	
	public boolean isDropCardExpired(long now)
	{
		if(now - this.yourTurnTimeStramp > ConstList.DROPCARDTIMEEXPIRED )
		{
			return true;
		}
		return false;
	}
	
	public int secsPassWhenYourTurn()
	{
		return (int)(ConstList.DROPCARDTIMEEXPIRED - (System.currentTimeMillis() - this.yourTurnTimeStramp) )/1000;
	}
	
	
	public void clearPukeInfo()
	{
		this.pkType.clear();
		this.fivePk.clear();
		this.pkLevel = 0;
	}
	
	private List<Puke> pkType = new ArrayList<Puke>();//玩家牌的集合
	
	private List<Puke> fivePk = new ArrayList<Puke>();//最大五张牌
	
	private int pkLevel = 0; //扑克牌的等级
	
	private long pkValue = 0L;
	
	private FivePukeItem maxFivePukeList = null;
	
	public void addPukes(List<Puke> map)
	{
		this.pkType.addAll(map);
	}
	
	public void addPuke(Puke puke)
	{
		this.pkType.add(puke);
	}
	
	public List<Puke> getPkType()
	{
		return this.pkType;
	}
	
	public long getPkValue()
	{
		return this.pkValue;
	}
	
	public List<Puke> getFivePk()
	{
		return this.fivePk;
	}
	
	public int getPkLevel()
	{
		return this.pkLevel;
	}
	
	public Puke getPuke(int index)
	{
		if(index < 0 || index >= this.pkType.size())
		{
			return null;
		}
		return this.pkType.get(index);
	}
	
	public void setPkLevelByPkType()
	{
		this.maxFivePukeList = PukerKit.getMaxFive(this.pkType);
		this.fivePk = this.maxFivePukeList.getList();
		this.pkValue = this.maxFivePukeList.getPkValue();
		this.pkLevel = this.maxFivePukeList.getLevel();
	}
	
	public ActionscriptObject toAsObj()
	{
		ActionscriptObject asObj = new ActionscriptObject();
		UserInfo uInfo = UserInfoMemoryCache.getUserInfo(this.userId);
		
		asObj.putNumber("sid",this.getSeatId());
		asObj.put("un",uInfo.getName());
		asObj.put("uid",this.getUserId());
		asObj.putNumber("tb",this.getTempBet());
		asObj.putNumber("yt",this.getYourTurn());
		asObj.putNumber("gs",this.getGameState().value());
		asObj.putNumber("cm",uInfo.getRmoney());
		
		return asObj;
	}
	
	public int getDropCardNum()
	{
		return this.dropCardNum;
	}
	
	public void clearDropCardNum()
	{
		this.dropCardNum = 0;
	}
	
	public void addDropCardNum()
	{
		this.dropCardNum ++;
	}
	
}
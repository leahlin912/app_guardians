package angelguardian.guardian.obj;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

public class Data {
    public static int wallet;
    public static int power;
    public static int bulletPerSec; //Bullet Percent Second
    public static double coinMultiplier;
    public static int gamelevel;
    private JSONObject jsonObject;

    public static final int stageOne = 1;
    public static final int stageTwo = 2;
    public static final int stageThree = 3;
    public static final int stageFour = 0;

    public Data(){
//        this.wallet = 0;
        this.power = 0;
        this.bulletPerSec = 0;
        this.coinMultiplier = 1;
//        this.gamelevel = 2;
        jsonObject = new JSONObject();
        try{
            jsonObject.put("WALLET",this.wallet);
            jsonObject.put("POWER",this.power);
            jsonObject.put("SPEED",this.bulletPerSec);
            jsonObject.put("COINMULTIPLIER",this.coinMultiplier);
            jsonObject.put("GAMELEVEL",this.gamelevel);
        }catch (JSONException e){

        }
    }

    public int getWallet(){
        return this.wallet;
    }

    public void setWallet(int wallet){
        this.wallet = wallet;
    }

    public int getSpeed(){
        return this.bulletPerSec;
    }

    public void upDataSpeed(int speed){
        this.bulletPerSec += speed;
    }

    public int getPower(){
        return this.power;
    }

    public void updatePower(int power){
        this.power += power;
    }

    public double getCoinMultiplier(){
        return this.coinMultiplier;
    }

    public void updateCoinMultiplier(){
        this.coinMultiplier = (this.coinMultiplier + 0.1);
    }

    public int getGamelevel(){ return this.gamelevel; }

    public void setGamelevel(int gamelevel){this.gamelevel = gamelevel; }

    public JSONObject getJson(){
        return this.jsonObject;
    }

    public void setJsonPause(){

        this.jsonObject = new JSONObject();
        try{
            jsonObject.put("WALLET",this.wallet);
            jsonObject.put("POWER",this.power);
            jsonObject.put("SPEED",this.bulletPerSec);
            jsonObject.put("COINMULTIPLIER",this.coinMultiplier);
            jsonObject.put("GAMELEVEL",this.gamelevel);
        }catch (JSONException e){

        }
        setJson(jsonObject);
        Log.d("DATA", "DATA JSON: " + jsonObject.toString());
    }

    public void setJson(JSONObject jsonObject){
        Log.d("SHAREDPREFERENCE", "GET_SHAREDPREFERENCE DATA: " + jsonObject.toString());

        try{

            this.wallet = Integer.parseInt(jsonObject.get("WALLET").toString());
            this.power = Integer.parseInt(jsonObject.get("POWER").toString());
            this.bulletPerSec = Integer.parseInt(jsonObject.get("SPEED").toString());
            this.coinMultiplier = Double.parseDouble(jsonObject.get("COINMULTIPLIER").toString());
            this.gamelevel = Integer.parseInt(jsonObject.get("GAMELEVEL").toString());

        }catch(JSONException e){

        }
    }

    public void reduceWallet(int money){
        this.wallet -= money;
    }

    public void increaseWallet(int money){
        this.wallet += money;
    }


}

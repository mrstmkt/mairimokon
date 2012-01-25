package jp.morishi.mairimokon.data;

import java.io.Serializable;

public class IRFrame  implements Serializable{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public static final String TABLE_NAME = "IRFRAME";
	public static final String COLUMN_RIMOKON_NO = "RIMOKON_NO";
	public static final String COLUMN_PANEL_NO = "PANEL_NO";
	public static final String COLUMN_BUTTON_NO = "BUTTON_NO";
	public static final String COLUMN_NO = "NO";
	public static final String COLUMN_FORMAT = "FORMAT";
	public static final String COLUMN_CARRIERHIGH = "CARRIERHIGH";
	public static final String COLUMN_CARRIERLOW = "CARRIERLOW";
	public static final String COLUMN_LEADERHIGH= "LEADERHIGH";
	public static final String COLUMN_LEADERLOW= "LEADERLOW";
	public static final String COLUMN_PULSE0MODULATION= "PULSE0MODULATION";
	public static final String COLUMN_PULSE0HIGH= "PULSE0HIGH";
	public static final String COLUMN_PULSE0LOW= "PULSE0LOW";
	public static final String COLUMN_PULSE1MODULATION= "PULSE1MODULATION";
	public static final String COLUMN_PULSE1HIGH= "PULSE1HIGH";
	public static final String COLUMN_PULSE1LOW= "PULSE1LOW";
	public static final String COLUMN_STOPHIGH= "STOPHIGH";
	public static final String COLUMN_STOPLOW= "STOPLOW";
	public static final String COLUMN_FRAMEINTERVAL= "FRAMEINTERVAL";
	public static final String COLUMN_REPEATHIGH= "REPEATHIGH";
	public static final String COLUMN_REPEATLOW= "REPEATLOW";
	public static final String COLUMN_VAL= "VAL";
	public static final String COLUMN_LEN= "LEN";
	
	public static final int FORMAT_NEC = 0;
    public static final int FORMAT_SONY = 1;
    public static final int FORMAT_DENKYO = 2;
    public static final int FORMAT_OTHER = 3;

    public static final int PPM_HIGH_LOW = 0;
    public static final int PPM_LOW_HIGH = 1;

    private int format;
    private IRFrameValue value;
    private int carrierHigh;
    private int carrierLow;
    private int leaderHigh;
    private int leaderLow;
    private int pulse0Modulation;
    private int pulse0High;
    private int pulse0Low;
    private int pulse1Modulation;
    private int pulse1High;
    private int pulse1Low;
    private int stopHigh;
    private int stopLow;
    private int frameInterval;
    private int repeatHigh;
    private int repeatLow;
    private MaiButtonInfo parent;
    
    public IRFrame()
    {
    	this.format = FORMAT_DENKYO;
    	this.value = new IRFrameValue();
    	this.carrierHigh = 0;
    	this.carrierLow = 0;
    	this.leaderHigh = 0;
    	this.leaderLow = 0;
    	this.pulse0Modulation = PPM_HIGH_LOW;
    	this.pulse0High = 0;
    	this.pulse0Low = 0;
    	this.pulse0Modulation = PPM_HIGH_LOW;
    	this.pulse1High = 0;
    	this.pulse0Low = 0;
    	this.stopHigh = 0;
    	this.stopLow = 0;
    	this.frameInterval = 0;
    	this.repeatHigh = 0;
    	this.repeatLow = 0;
    	this.parent = null;
    }
	public int getFormat() {
		return format;
	}
	public void setFormat(int format) {
		this.format = format;
	}
	public IRFrameValue getValue() {
		return value;
	}
	public void setValue(IRFrameValue value) {
		this.value = value;
	}
	public int getCarrierHigh() {
		return carrierHigh;
	}
	public void setCarrierHigh(int carrierHigh) {
		this.carrierHigh = carrierHigh;
	}
	public int getCarrierLow() {
		return carrierLow;
	}
	public void setCarrierLow(int carrierLow) {
		this.carrierLow = carrierLow;
	}
	public int getLeaderHigh() {
		return leaderHigh;
	}
	public void setLeaderHigh(int leaderHigh) {
		this.leaderHigh = leaderHigh;
	}
	public int getLeaderLow() {
		return leaderLow;
	}
	public void setLeaderLow(int leaderLow) {
		this.leaderLow = leaderLow;
	}
	public int getPulse0Modulation() {
		return pulse0Modulation;
	}
	public void setPulse0Modulation(int pulse0Modulation) {
		this.pulse0Modulation = pulse0Modulation;
	}
	public int getPulse0High() {
		return pulse0High;
	}
	public void setPulse0High(int pulse0High) {
		this.pulse0High = pulse0High;
	}
	public int getPulse0Low() {
		return pulse0Low;
	}
	public void setPulse0Low(int pulse0Low) {
		this.pulse0Low = pulse0Low;
	}
	public int getPulse1Modulation() {
		return pulse1Modulation;
	}
	public void setPulse1Modulation(int pulse1Modulation) {
		this.pulse1Modulation = pulse1Modulation;
	}
	public int getPulse1High() {
		return pulse1High;
	}
	public void setPulse1High(int pulse1High) {
		this.pulse1High = pulse1High;
	}
	public int getPulse1Low() {
		return pulse1Low;
	}
	public void setPulse1Low(int pulse1Low) {
		this.pulse1Low = pulse1Low;
	}
	public int getStopHigh() {
		return stopHigh;
	}
	public void setStopHigh(int stopHigh) {
		this.stopHigh = stopHigh;
	}
	public int getStopLow() {
		return stopLow;
	}
	public void setStopLow(int stopLow) {
		this.stopLow = stopLow;
	}
	public int getFrameInterval() {
		return frameInterval;
	}
	public void setFrameInterval(int frameInterval) {
		this.frameInterval = frameInterval;
	}
	public int getRepeatHigh() {
		return repeatHigh;
	}
	public void setRepeatHigh(int repeatHigh) {
		this.repeatHigh = repeatHigh;
	}
	public int getRepeatLow() {
		return repeatLow;
	}
	public void setRepeatLow(int repeatLow) {
		this.repeatLow = repeatLow;
	}
	public void setParent(MaiButtonInfo parent) {
		this.parent = parent;
	}
	public MaiButtonInfo getParent() {
		return parent;
	}
    
    
}

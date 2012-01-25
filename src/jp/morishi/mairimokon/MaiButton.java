package jp.morishi.mairimokon;


import java.util.ArrayList;

import android.content.Context;
import android.content.res.Resources;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import jp.co.sharp.android.io.irrc.IrRemoteController;
import jp.co.sharp.android.io.irrc.IrRemoteControllerException;
import jp.co.sharp.android.io.irrc.IrRemoteControllerFrame;
import jp.morishi.mairimokon.data.MaiButtonInfo;
import jp.morishi.mairimokon.data.IRFrame;

public class MaiButton extends LinearLayout{
	public static final int TYPE1 = 0;
	public static final int TYPE2 = 1;
	public static final int TYPE3 = 2;
	
	private Display display;
	private int type;
	private TextView upperLabelText;
	private Button button;
	private MaiButtonInfo buttonInfo;
	private LongPushSendThread longPushThread;
	
	public MaiButton(Context context) {
		super(context);
		this.display = ((WindowManager)context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
		this.buttonInfo = new MaiButtonInfo();
		this.longPushThread = null;
		setType(TYPE1);
		init(context, null);
	}
	
	public MaiButton(Context context,  AttributeSet attrs)
	{
		super(context, attrs);
		if(isInEditMode() == false)
		{
			WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
			this.display = wm.getDefaultDisplay();
		}
		this.buttonInfo = new MaiButtonInfo();
		initAttribute(attrs);
		init(context, attrs);
	}

	private void initAttribute(AttributeSet attrs){
		int type = attrs.getAttributeIntValue(null, "type", TYPE1);
		if(type != TYPE1 && type != TYPE2 && type != TYPE3)
		{
			this.setType(TYPE1);
		}
		else
		{
			this.setType(type);
		}
	}
	protected void init(Context context,  AttributeSet attrs)
	{
		setOrientation(VERTICAL);
		setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
		setPadding(1, 0, 1, 0);
		// テキストの生成
		if(attrs != null)
		{
			upperLabelText = new TextView( context, attrs);
		}
		else
		{
			upperLabelText = new TextView( context);
		}
		upperLabelText.setText("test");
		upperLabelText.setGravity(Gravity.CENTER);
		addView(upperLabelText);
		
		// ボタンの生成
		if(attrs != null)
		{
			button = new Button( context, attrs);
		}
		else
		{
			button = new Button( context);
		}
		button.setText("test");
		button.setGravity(Gravity.CENTER);
		button.setPadding(1, 0, 1, 0);
		button.setBackgroundResource(R.drawable.button_default_stateful);
		addView( button, new LinearLayout.LayoutParams( LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
		
		if(isInEditMode() == false)
		{
			switch(this.type)
			{
			case TYPE1:
				this.button.setWidth(display.getWidth()/3 - 3);
				this.button.setHeight(display.getHeight()/8);
				break;
			case TYPE2:
				setPadding(10, 5, 10, 5);
				int height = display.getHeight()/10;
				this.button.setHeight(height);
				this.button.setWidth(height);
				break;
			case TYPE3:
				this.button.setWidth(display.getWidth()/4 - 3);
				this.button.setHeight(display.getHeight()/20);
				break;
			}
		}
	}
	

	private void setType(int type) {
		this.type = type;
	}

	public int getType() {
		return type;
	}

	public void setButtonInfo(MaiButtonInfo buttonInf) {
		this.buttonInfo = buttonInf;
		if(this.buttonInfo != null)
		{
			this.upperLabelText.setText(this.buttonInfo.getUpperLabel());
			this.button.setText(this.buttonInfo.getInnerLabel());
			setButtonColor(this.buttonInfo.getColor());
			if(this.buttonInfo.isDisable())
			{
				this.button.setVisibility(INVISIBLE);
			}
			else
			{
				if(this.buttonInfo.isLongPush() == false)
				{
					this.button.setOnClickListener(new OnClickListener() {
						
						public void onClick(View v) {
							Vibrator vib = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
							vib.vibrate(100);
							Log.v("ButtonClick", "ButtonClicked");
							sendIRFrames(false);
							animate();
						}
					});
				}
				else
				{
					this.button.setOnTouchListener(new OnTouchListener() {
						
						public boolean onTouch(View v, MotionEvent event) {
							Vibrator vib = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
							vib.vibrate(100);
							if(event.getAction() == MotionEvent.ACTION_DOWN)
							{
								Log.v("TouchMotion", "ACTION_DOWN");
								if(buttonInfo.getFrames() != null && 
										buttonInfo.getFrames().size() > 0)
								{
									longPushThread = new LongPushSendThread(buttonInfo.getFrames().get(0).getFrameInterval());
									longPushThread.start();
								}
							}
							else if(event.getAction()== MotionEvent.ACTION_UP)
							{
								Log.v("TouchMotion", "ACTION_UP");
								if(longPushThread != null)
								{
									longPushThread.sendStop();
									try {
										longPushThread.join();
									} catch (InterruptedException e) {
										e.printStackTrace();
									}
									finally
									{
										longPushThread = null;
									}
								}
							}
							else
							{
								Log.v("TouchMotion", "Event:" + event.getAction());
							}
							return true;
						}
					});
				}
			}
		}
	}

	public MaiButtonInfo getButtonInfo() {
		return buttonInfo;
	}
	
	private void setButtonColor(int color)
	{
		Resources res = getResources();
		switch(color)
		{
		case MaiButtonInfo.COLOR_DEFAULT:
			this.button.setTextColor(res.getColorStateList(R.color.button_default_text_color));
			this.button.setBackgroundResource(R.drawable.button_default_stateful);
			break;
		case MaiButtonInfo.COLOR_BLUE:
			this.button.setTextColor(res.getColorStateList(R.color.button_blue_text_color));
			this.button.setBackgroundResource(R.drawable.button_blue_stateful);
			break;
		case MaiButtonInfo.COLOR_RED:
			this.button.setTextColor(res.getColorStateList(R.color.button_red_text_color));
			this.button.setBackgroundResource(R.drawable.button_red_stateful);
			break;
		case MaiButtonInfo.COLOR_GREEN:
			this.button.setTextColor(res.getColorStateList(R.color.button_green_text_color));
			this.button.setBackgroundResource(R.drawable.button_green_stateful);
			break;
		case MaiButtonInfo.COLOR_YELLOW:
			this.button.setTextColor(res.getColorStateList(R.color.button_yellow_text_color));
			this.button.setBackgroundResource(R.drawable.button_yellow_stateful);
			break;
		default:
			this.button.setBackgroundResource(R.drawable.button_default_stateful);
			break;
		}
		
	}
	
	private void sendIRFrames(boolean repeat)
	{
		ArrayList<IRFrame> list = this.buttonInfo.getFrames();
		if(list != null && list.size() > 0 )
		{
			/* リモコンデータ */
			IrRemoteController rc = null;
	        try {
				rc = new IrRemoteController(this.button.getContext());
	        } catch (NullPointerException e) {
	        } catch (RuntimeException e) {
	        }
	        if(rc != null)
	        {
	        	try
	        	{
					rc.setCarrier(list.get(0).getCarrierHigh() * 10, list.get(0).getCarrierLow() * 10); // リモコンデータ：キャリア
					int pulse0Modulation;
					if(list.get(0).getPulse0Modulation() == IRFrame.PPM_HIGH_LOW)
					{
						pulse0Modulation = IrRemoteController.PPM_HIGH_LOW;
					}
					else
					{
						pulse0Modulation = IrRemoteController.PPM_LOW_HIGH;
					}
					int pulse1Modulation;
					if(list.get(0).getPulse1Modulation() == IRFrame.PPM_HIGH_LOW)
					{
						pulse1Modulation = IrRemoteController.PPM_HIGH_LOW;
					}
					else
					{
						pulse1Modulation = IrRemoteController.PPM_LOW_HIGH;
					}
					rc.setPulse0(pulse0Modulation, list.get(0).getPulse0High(), list.get(0).getPulse0Low()); // 論理パルス0
					rc.setPulse1(pulse1Modulation, list.get(0).getPulse1High(), list.get(0).getPulse1Low()); // 論理パルス1
					
					/* ブロックデータ */
					if(repeat == true && this.buttonInfo.getFormat() == IRFrame.FORMAT_NEC)
					{
						IrRemoteControllerFrame[] block = new IrRemoteControllerFrame[1];
						IrRemoteControllerFrame frame = new IrRemoteControllerFrame();
						frame.setLeader(list.get(0).getLeaderHigh(), list.get(0).getLeaderLow()); // リーダー
						frame.setFrameData(new byte[0], 0);
						frame.setTrailer(list.get(0).getStopHigh()); // トレーラー
						frame.setFrameLength(list.get(0).getFrameInterval()); // フレームの時間間隔
						frame.setRepeatCount(1); // 繰り返し回数
						block[0] = frame;
						rc.send(block, 1);
						Log.v("SendIRFrames", 1 + "Frames Send");
					}
					else
					{
						ArrayList<IrRemoteControllerFrame> blockList = new ArrayList<IrRemoteControllerFrame>();
						for(IRFrame frameData:list)
						{
							ArrayList<Byte> valueList = frameData.getValue().getValueList();
							if(valueList != null && valueList.size() > 0)
							{
								IrRemoteControllerFrame frame = new IrRemoteControllerFrame();
								frame.setLeader(frameData.getLeaderHigh(), frameData.getLeaderLow()); // リーダー
								byte values[] = new byte[valueList.size()];
								int j = 0;
								for(byte b:valueList)
								{
									values[j] = b;
									j++;
								}
								frame.setFrameData(values, frameData.getValue().getValueLength()); // データ
								frame.setTrailer(frameData.getStopHigh()); // トレーラー
								frame.setFrameLength(frameData.getFrameInterval()); // フレームの時間間隔
								frame.setRepeatCount(1); // 繰り返し回数

								blockList.add(frame);
							}
						}
						IrRemoteControllerFrame[] block = new IrRemoteControllerFrame[blockList.size()];
						for(int i= 0; i < block.length; i++)
						{
							block[i] = blockList.get(i);
						}
						rc.send(block, block.length);
						Log.v("SendIRFrames", block.length + "Frames Send");
					}
	        		
	        	}
	        	catch(IrRemoteControllerException e)
	        	{
					Log.v("IrRemoteControllerException", e.getMessage());
	        	}
	        }

		}
	}
	
	private void animate()
	{
		// 【1】インスタンスを生成
	    AnimationSet set = new AnimationSet(true);
	 
	    // 【2】基本のアニメーションを生成
	    ScaleAnimation scale1 = new ScaleAnimation(1, 0.8f, 1, 0.8f, this.getWidth()/2, this.getHeight()/2);
	    ScaleAnimation scale2 = new ScaleAnimation(0.8f, 1, 0.8f, 1, this.getWidth()/2, this.getHeight()/2);
	    ScaleAnimation scale3 = new ScaleAnimation(1, 1.2f, 1, 1.2f, this.getWidth()/2, this.getHeight()/2);
	    ScaleAnimation scale4 = new ScaleAnimation(1.2f, 1, 1.2f, 1, this.getWidth()/2, this.getHeight()/2);
	 
	    // 【3】生成したアニメーションを追加
	    set.addAnimation(scale1); 
	    set.addAnimation(scale2);
	    set.addAnimation(scale3);
	    set.addAnimation(scale4);
	 
	    // 【4】アニメーション時間を設定して動作開始
	    set.setDuration(150);
	    this.bringToFront();
	    this.startAnimation(set);
	}
	class LongPushSendThread extends Thread
	{
		private int interval;
		private boolean stop;
		public LongPushSendThread(int interval)
		{
			this.interval = interval;
			this.stop = false;
		}
		@Override
		public void run() {
			boolean start = true;
			long micro = 0;
			while(this.stop == false)
			{
				if(start == true)
				{
					start = false;
					sendIRFrames(false);
				}
				else
				{
					long now = System.nanoTime()/1000;
					if(now > micro + interval)
					{
						sendIRFrames(true);
					}
				}
				micro = System.nanoTime()/1000;
			}
		}
		public void sendStop()
		{
			this.stop = true;
		}
	}

}

package com.echoesnet.eatandmeet.views.widgets;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.DisplayMetrics;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.utils.CommonUtils;
import com.orhanobut.logger.Logger;

import java.lang.ref.WeakReference;

public class DialogUtil
{
	public static Dialog getCommonDialog(Activity context,String description)
	{
		final Dialog dialog = new Dialog(context, R.style.Dialog02);
		dialog.setContentView(R.layout.firset_dialog_view);
		Window window = dialog.getWindow();
		WindowManager.LayoutParams lp = window.getAttributes();

		int screenW = getScreenWidth(context);
		lp.width = (int) (0.8 * screenW);
		lp.height = CommonUtils.dp2px(context,80);

		final ProcessOutTime processOutTime = new ProcessOutTime(context,dialog);
		dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface dialog) {
				if (processOutTime!=null)
					processOutTime.stop();
			}
		});
		dialog.setOnShowListener(new DialogInterface.OnShowListener() {
			@Override
			public void onShow(DialogInterface dialog) {
				if (processOutTime!=null)
					processOutTime.start();
			}
		});

		TextView tvDescribe = (TextView) dialog.findViewById(R.id.tvLoad);
		tvDescribe.setText(description);
		return dialog;
	}

	public static Dialog getCustomDialog(Activity context) {
		final Dialog dialog = new Dialog(context, R.style.Dialog02);
		return dialog;
	}
	/**
	 * 非activity的context获取自定义对话框
	 * @param context
	 * @return
	 */
	public static Dialog getWinDialog(Context context) {
		final Dialog dialog = new Dialog(context, R.style.Dialog02);
		dialog.getWindow()
				.setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
		return dialog;
	}

	public static int getScreenWidth(Activity context) {
		DisplayMetrics dm = new DisplayMetrics();
		context.getWindowManager().getDefaultDisplay().getMetrics(dm);
		return dm.widthPixels;
	}

	public static int getScreenHeight(Activity context) {
		DisplayMetrics dm = new DisplayMetrics();
		context.getWindowManager().getDefaultDisplay().getMetrics(dm);
		return dm.heightPixels;
	}
	/*超时线程*/
	private static class ProcessOutTime implements Runnable
	{
		private boolean running = false;
		private long startTime = 0L;
		private Thread thread = null;
		private final WeakReference<Activity> mActRef;
		private final WeakReference<Dialog> pDialogRef;

		public ProcessOutTime(Activity mAct,Dialog dialog)
		{
			mActRef = new WeakReference<Activity>(mAct);
			pDialogRef = new WeakReference<Dialog>(dialog);
		}

		public void run()
		{
			while (true)
			{
				if (!this.running)
					return;
				if (System.currentTimeMillis() - this.startTime > 15 * 1000L)
				{
					final Activity cAct = mActRef.get();
					final Dialog pDialog = pDialogRef.get();
					if (cAct != null)
					{
						cAct.runOnUiThread(new Runnable()
						{
							@Override
							public void run()
							{
								if (pDialog != null && pDialog.isShowing())
								{
									Logger.t("dialog>").d("dialog.dis");
									try
									{
										pDialog.dismiss();
									} catch (Exception e)
									{
										e.printStackTrace();
										Logger.t("dialog>").d("pDialog.dismiss()失败");
									}
//									ToastUtils.showShort(cAct.mAct, "程序出现了不可抗拒的错误，关闭页面重试下...");
								}
							}
						});
					}
					this.running = false;
					this.thread = null;
					this.startTime = 0L;
				}
				try
				{
					Thread.sleep(200L);
				} catch (Exception localException)
				{
				}
			}
		}

		public void start()
		{
			try
			{
				this.thread = new Thread(this);
				this.running = true;
				this.startTime = System.currentTimeMillis();
				this.thread.start();
			} catch (Exception e)
			{
				Logger.t("Dialog").d(e.getMessage());
			} finally
			{
			}
		}

		public void stop()
		{
			this.running = false;
			this.thread = null;
			this.startTime = 0L;
		}
	}
}

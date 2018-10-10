///*******************************************************************************
// * Copyright 2011, 2012 Chris Banes.
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// * http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// *******************************************************************************/
//package com.handmark.pulltorefresh.library.internal;
//
//import android.content.Context;
//import android.content.res.TypedArray;
//import android.graphics.drawable.Drawable;
//
//import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
//import com.handmark.pulltorefresh.library.PullToRefreshBase.Orientation;
//import com.handmark.pulltorefresh.library.R;
//
//public class MyLoadingLayout extends LoadingLayout {
//
//	static final int ROTATION_ANIMATION_DURATION = 1200;
//
//
//	private Context mContext;
//
//	public MyLoadingLayout(Context context, Mode mode, Orientation scrollDirection, TypedArray attrs) {
//		super(context, mode, scrollDirection, attrs);
//		this.mContext=context;
//	}
//
//	public void onLoadingDrawableSet(Drawable imageDrawable) {
//
//	}
//
//	protected void onPullImpl(float scaleOfLayout) {
///*		float angle;
//		if (mRotateDrawableWhilePulling) {
//			angle = scaleOfLayout * 90f;
//		} else {
//			angle = Math.max(0f, Math.min(180f, scaleOfLayout * 360f - 180f));
//		}
//
//		mHeaderImageMatrix.setRotate(angle, mRotationPivotX, mRotationPivotY);
//		mHeaderImage.setImageMatrix(mHeaderImageMatrix);*/
//	}
//
//	@Override
//	protected void refreshingImpl() {
//		//mHeaderImage.startAnimation(mRotateAnimation);
//	}
//
//	@Override
//	protected void resetImpl() {
//		//mHeaderImage.clearAnimation();
//		//resetImageRotation();
//	}
//
//	private void resetImageRotation() {
///*		if (null != mHeaderImageMatrix) {
//			mHeaderImageMatrix.reset();
//			mHeaderImage.setImageMatrix(mHeaderImageMatrix);
//		}*/
//	}
//
//	@Override
//	protected void pullToRefreshImpl() {
//		// NO-OP
//	}
//
//	@Override
//	protected void releaseToRefreshImpl() {
//		// NO-OP
//	}
//
//	@Override
//	protected int getDefaultDrawableResId() {
//		return R.drawable.loading;
//	}
//}

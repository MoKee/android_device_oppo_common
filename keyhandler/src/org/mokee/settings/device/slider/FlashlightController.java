/*
 * Copyright (C) 2017 The MoKee Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.mokee.settings.device.slider;

import android.content.Context;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.os.Handler;
import android.util.Log;

import org.mokee.settings.device.SliderControllerBase;

public final class FlashlightController extends SliderControllerBase {

    public static final int ID = 2;

    private static final String TAG = "FlashlightController";

    private static final int FLASHLIGHT_OFF = 20;
    private static final int FLASHLIGHT_ON = 21;
    private static final int FLASHLIGHT_BLINK = 22;

    private static final long BLINK_INTERVAL = 250L;

    private final CameraManager mCameraManager;
    private final String mCameraId;

    private boolean mTorchEnabled = false;

    private final Handler mBlinkHandler = new Handler();
    private final Runnable mBlinkRunnble = new Runnable() {
        @Override
        public void run() {
            setTorchMode(!mTorchEnabled);
            mBlinkHandler.postDelayed(this, BLINK_INTERVAL);
        }
    };

    public FlashlightController(Context context) {
        super(context);
        mCameraManager = getSystemService(Context.CAMERA_SERVICE);
        mCameraId = getCameraId();
    }

    @Override
    protected boolean processAction(int action) {
        Log.d(TAG, "slider action: " + action);
        switch (action) {
            case FLASHLIGHT_OFF:
                setTorchMode(false);
                mBlinkHandler.removeCallbacksAndMessages(null);
                return true;
            case FLASHLIGHT_ON:
                setTorchMode(true);
                mBlinkHandler.removeCallbacksAndMessages(null);
                return true;
            case FLASHLIGHT_BLINK:
                mBlinkHandler.removeCallbacksAndMessages(null);
                mBlinkHandler.post(mBlinkRunnble);
                return true;
            default:
                return false;
        }
    }

    @Override
    public void reset() {
        mBlinkHandler.removeCallbacksAndMessages(null);
        setTorchMode(false);
    }

    private void setTorchMode(boolean enabled) {
        if (mCameraId == null) {
            return;
        }

        try {
            mCameraManager.setTorchMode(mCameraId, enabled);
            mTorchEnabled = enabled;
        } catch (CameraAccessException ignored) {
        }
    }

    private String getCameraId() {
        try {
            for (final String cameraId : mCameraManager.getCameraIdList()) {
                final CameraCharacteristics characteristics =
                        mCameraManager.getCameraCharacteristics(cameraId);
                final int orientation = characteristics.get(CameraCharacteristics.LENS_FACING);
                if (orientation == CameraCharacteristics.LENS_FACING_BACK) {
                    return cameraId;
                }
            }
        } catch (CameraAccessException ignored) {
        }

        return null;
    }
}

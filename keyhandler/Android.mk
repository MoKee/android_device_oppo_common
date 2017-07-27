LOCAL_PATH:= $(call my-dir)

ifeq ($(strip $(TARGET_USES_OPPO_CONFIGPANEL)), true)

include $(CLEAR_VARS)

LOCAL_MODULE := com.cyanogenmod.keyhandler
LOCAL_SRC_FILES := $(call all-java-files-under,src)
LOCAL_MODULE_TAGS := optional
LOCAL_DEX_PREOPT := false
LOCAL_STATIC_JAVA_LIBRARIES := org.mokee.platform.internal
include $(BUILD_JAVA_LIBRARY)

endif

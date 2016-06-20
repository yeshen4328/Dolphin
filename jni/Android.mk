LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE    := AudioRecord
LOCAL_SRC_FILES := AudioRecord.cpp

include $(BUILD_SHARED_LIBRARY)

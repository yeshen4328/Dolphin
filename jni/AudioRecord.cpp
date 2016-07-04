#include <jni.h>
#include <jni.h>
#include <String.h>
#include <stdlib.h>
#include <stdio.h>
#include <android/log.h>
#include <math.h>
#define  LOG_TAG    "Clog"
#define  LOGI(...)  __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)
extern "C"
{
	jdoubleArray
	Java_mathTools_DFT_dft(JNIEnv* env, jobject obj, jdoubleArray  inreal, jdoubleArray inimag, jint N)
	{
		jdouble* realArr = env->GetDoubleArrayElements(inreal, 0);
		jdouble* imagArr = env->GetDoubleArrayElements(inimag, 0);
		jdouble outReal[N] = {};
		jdouble outImag[N] = {};
		for (int k = 0; k < N; k++) {  /* For each output element */
			double sumreal = 0;
			double sumimag = 0;
			for (int t = 0; t < N; t++) {  /* For each input element */
				double angle = 2 * 3.1415926 * t * k / N;
				sumreal += realArr[t] * cos(angle) + imagArr[t] * sin(angle);
				sumimag += -realArr[t] * sin(angle) + imagArr[t] * cos(angle);
			}
			outReal[k] = sumreal;
			outImag[k] = sumimag;
		}
		jdoubleArray out = env->NewDoubleArray(2 * N);
		env->SetDoubleArrayRegion(out, 0, N, outReal);
		env->SetDoubleArrayRegion(out, N, N, outImag);
		return out;
	}
}

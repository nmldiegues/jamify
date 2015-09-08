package pt.trycatch.jamify.learner;

/**
 * Animation class.
 */
public abstract class Animation {
	public float mStartValue;
	public float mEndValue;
	public long mDuration = 0;
	public long mStartTime = 0;
	public long mEndTime = 0;
	public Type mType;

	public enum Type {
		Translate, Alpha, Scale, Rotate,
	};

	public Animation(Type type) {
		mType = type;
	}

	public boolean isStarted() {
		if (System.currentTimeMillis() >= mStartTime) {
			return true;
		}
		return false;
	}

	public boolean isEnded() {
		if (System.currentTimeMillis() >= mEndTime) {
			return true;
		}
		return false;
	}

	public float getCurrentValue(long currentTime) {
		float currentValue = mStartValue + (currentTime)
				* (mEndValue - mStartValue) / mDuration;
		if (currentTime > mEndTime) {
			currentValue = mEndValue;
		}
		return currentValue;
	}
}

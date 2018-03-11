package au.net.projectb.auto.modes;

import au.net.projectb.auto.AutoController.FieldPosition;

// Uses the naming convention "startPosition" "Scale/Switch + Left/Right"
// e.g. LeftScaleRight starts on the Left and does ScaleRight
public abstract class AutoMode {
	@Deprecated
	public abstract void setFieldPositions(FieldPosition startPos, FieldPosition switchPos, FieldPosition scalePos);
	public abstract void runStep(int stepIndex);
	public abstract boolean getStepIsCompleted(int stepIndex);
}

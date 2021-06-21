package net.square.intect.checks.checks.pattern;

import net.square.intect.checks.objectable.Check;
import net.square.intect.checks.objectable.CheckInfo;
import net.square.intect.checks.objectable.IntectPacket;
import net.square.intect.processor.custom.RotationProcessor;
import net.square.intect.processor.data.PlayerStorage;

@CheckInfo(name = "Pattern", type = "Liquid", maxVL = 5, description = "Checks for liquid heuristics")
public class PatternTypeLiquid extends Check
{

    public PatternTypeLiquid(PlayerStorage data)
    {
        super(data);
    }

    private int threshold = 0;

    private double lastLiquidYaw = 0.0;
    private double lastLiquidPitch = 0.0;

    @Override
    public void handle(IntectPacket packet)
    {

        if (shouldBypass()) return;

        RotationProcessor processor = getStorage().getRotationProcessor();

        double f = processor.getFinalSensitivity() * 0.6F + 0.2F;
        double gcd = f * f * f * 1.2F;

        double deltaYaw = processor.getDeltaYaw() % gcd;
        double deltaPitch = processor.getDeltaPitch() % gcd;

        double yaw = deltaYaw - this.lastLiquidYaw;
        double pitch = deltaPitch - this.lastLiquidPitch;

        if (pitch != 0.0 && yaw != 0.0)
        {
            debug(String.format("pitch=%.5f yaw=%.5f", Math.abs(pitch), Math.abs(yaw)));
        }

        if (pitch > 20.0 && yaw > 0.0)
        {
            //if (threshold++ > 0) {
            getPlayer().sendMessage("§c§lLIQUID PATTERN (GCD FLAW BYPASS)");
            //}
        }
        else
        {
            threshold -= threshold > 0 ? 1 : 0;
        }

        this.lastLiquidYaw = deltaYaw;
        this.lastLiquidPitch = deltaPitch;
    }
}

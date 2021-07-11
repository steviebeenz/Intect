package net.square.intect.utils.objectable;

import net.square.intect.checks.objectable.CheckInfo;

public interface IntectHandler
{

    CheckInfo getCheckInfo();

    boolean isEnabled();
}

package com.phasetranscrystal.nonard.opesystem;

import com.phasetranscrystal.nonatomic.core.Operator;
import com.phasetranscrystal.nonatomic.core.OperatorInfo;
import com.phasetranscrystal.nonatomic.core.OperatorType;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.Optional;

public class ArknightsOperator extends Operator {
    public ArknightsOperator(OperatorType operatorType) {
        super(operatorType);
    }

    public ArknightsOperator(Identifier identifier) {
        super(identifier);
    }

    public ArknightsOperator(Identifier identifier, List<? extends OperatorInfo> infos, Optional<EntityFinderInfo> entityFinderInfo, boolean redeployFlag, ResourceLocation status) {
        super(identifier, infos, entityFinderInfo, redeployFlag, status);
    }
}

package com.phasetranscrystal.nonard.opesystem.skill;

import com.mojang.serialization.MapCodec;
import com.phasetranscrystal.blast.skill.Skill;
import com.phasetranscrystal.nonard.opesystem.ArknightsOperatorType;
import com.phasetranscrystal.nonard.opesystem.OperatorEntity;
import com.phasetranscrystal.nonatomic.core.OperatorInfo;
import it.unimi.dsi.fastutil.objects.Object2IntMap;

public class OperatorSkillData extends OperatorInfo {
//    public final OperatorData belonging;
    protected int checked;
    protected Object2IntMap<Skill<?>> levelBySkill;

    @Override
    public MapCodec<OperatorSkillData> codec() {
        return null;
    }

    @Override
    public <D extends OperatorInfo> boolean merge(D newData) {
        return false;
    }

    @Override
    public <D extends OperatorInfo> D createExternal() {
        return null;
    }

    @Override
    public <T extends OperatorInfo> T copy() {
        return null;
    }
}

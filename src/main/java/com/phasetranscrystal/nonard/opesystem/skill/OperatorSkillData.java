package com.phasetranscrystal.nonard.opesystem.skill;

import com.mojang.serialization.MapCodec;
import com.phasetranscrystal.blast.skill.Skill;
import com.phasetranscrystal.nonatomic.core.OperatorInfo;
import it.unimi.dsi.fastutil.objects.Object2IntMap;

public class OperatorSkillData extends OperatorInfo<OperatorSkillData> {

    // public final OperatorData belonging;
    protected int checked;
    protected Object2IntMap<Skill<?>> levelBySkill;

    @Override
    public MapCodec<OperatorSkillData> codec() {
        return null;
    }

    @Override
    public boolean merge(OperatorSkillData newData) {
        return false;
    }

    @Override
    public OperatorSkillData createExternal() {
        return null;
    }

    @Override
    public OperatorSkillData copy() {
        return null;
    }
}

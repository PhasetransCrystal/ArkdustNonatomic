package com.phasetranscrystal.nonard.migrate.ardcore.econ;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.phasetranscrystal.nonard.ArkdustNonatomic;
import com.phasetranscrystal.nonard.migrate.ardcore.ArkdustCore;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.EntityCapability;
import net.neoforged.neoforge.capabilities.ItemCapability;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class Account {
    public static final Codec<Account> CODEC = RecordCodecBuilder.create(i -> i.group(
            Codec.LONG.fieldOf("lmb").forGetter(Account::getLmb),
            Codec.LONG.fieldOf("originitePrime").forGetter(Account::getOriginitePrime),
            Codec.LONG.fieldOf("originiumIngot").forGetter(Account::getOriginiumIngot),
            Codec.LONG.fieldOf("orundum").forGetter(Account::getOrundum)
    ).apply(i, Account::new));

    private long lmb;
    private long originitePrime;
    private long originiumIngot;
    private long orundum;

    public Account(long lmb, long originitePrime, long originiumIngot, long orundum) {
        setLmb(lmb);
        setOriginitePrime(originitePrime);
        setOriginiumIngot(originiumIngot);
        setOrundum(orundum);
    }

    public Account() {
        lmb = 0;
        originitePrime = 0;
        originiumIngot = 0;
        orundum = 0;
    }

    public long getLmb() {
        return lmb;
    }

    public void setLmb(long lmb) {
        this.lmb = Math.max(lmb, 0);
    }

    public void insertLmb(long lmb) {
        this.lmb += lmb;
    }

    public long extractLmb(long lmb) {
        if(this.lmb >= lmb){
            this.lmb -= lmb;
        } else {
            lmb = this.lmb;
            this.lmb = 0;
        }
        return lmb;
    }

    public long getOriginitePrime() {
        return originitePrime;
    }

    public void setOriginitePrime(long originitePrime) {
        this.originitePrime = Math.max(0, originitePrime);
    }

    public void insertOriginitePrime(long originitePrime) {
        this.originitePrime += originitePrime;
    }

    public long extractOriginitePrime(long originitePrime) {
        if(this.originitePrime >= originitePrime){
            this.originitePrime -= originitePrime;
        } else {
            originitePrime = this.originitePrime;
            this.originitePrime = 0;
        }
        return originitePrime;
    }

    public long getOriginiumIngot() {
        return originiumIngot;
    }

    public void setOriginiumIngot(long originiumIngot) {
        this.originiumIngot = Math.max(0, originiumIngot);
    }

    public void insertOriginiumIngot(long originiumIngot) {
        this.originiumIngot += originiumIngot;
    }

    public long extractOriginiumIngot(long originiumIngot) {
        if(this.originiumIngot >= originiumIngot){
            this.originiumIngot -= originiumIngot;
        } else {
            originiumIngot = this.originiumIngot;
            this.originiumIngot = 0;
        }
        return originiumIngot;
    }

    public long getOrundum() {
        return orundum;
    }

    public void setOrundum(long orundum) {
        this.orundum = Math.max(0, orundum);
    }

    public void insertOrundum(long orundum) {
        this.orundum += orundum;
    }

    public long extractOrundum(long orundum) {
        if(this.orundum >= orundum){
            this.orundum -= orundum;
        } else {
            orundum = this.orundum;
            this.orundum = 0;
        }
        return orundum;
    }

    public boolean allMatched(Requirement requirement) {
        return this.lmb >= requirement.lmb &&
                this.originitePrime >= requirement.originitePrime &&
                this.originiumIngot >= requirement.originiumIngot &&
                this.orundum >= requirement.orundum;
    }

    public boolean extract(Requirement requirement) {
        if (allMatched(requirement)) {
            this.lmb -= requirement.lmb;
            this.originitePrime -= requirement.originitePrime;
            this.originiumIngot -= requirement.originiumIngot;
            this.orundum -= requirement.orundum;
            return true;
        }
        return false;
    }

    public Optional<Requirement> extractWithFallBack(Requirement requirement) {
        this.lmb -= requirement.lmb;
        this.originitePrime -= requirement.originitePrime;
        this.originiumIngot -= requirement.originiumIngot;
        this.orundum -= requirement.orundum;

        boolean flag = false;
        Requirement fallback = requirement.copy();
        long cache;

        cache = extractLmb(requirement.lmb);
        if(cache != requirement.lmb){
            fallback.setLmb(requirement.lmb - cache);
            flag = true;
        }

        cache = extractOriginitePrime(requirement.originitePrime);
        if(cache != requirement.originitePrime){
            fallback.setOriginitePrime(requirement.originitePrime - cache);
            flag = true;
        }

        cache = extractOriginiumIngot(requirement.originiumIngot);
        if(cache != requirement.originiumIngot){
            fallback.setOriginiumIngot(requirement.originiumIngot - cache);
            flag = true;
        }

        cache = extractOrundum(requirement.orundum);
        if(cache != requirement.orundum){
            fallback.setOrundum(requirement.orundum - cache);
            flag = true;
        }

        if(flag){
            return Optional.of(fallback);
        }
        return Optional.empty();
    }

    public static class Capability {
        public static final BlockCapability<Account, @Nullable Direction> BLOCK = BlockCapability.createSided(ArkdustCore.location("account"), Account.class);
        public static final EntityCapability<Account, @Nullable Direction> ENTITY = EntityCapability.createSided(ArkdustCore.location("account"), Account.class);
        public static final ItemCapability<Account, @Nullable Void> ITEM = ItemCapability.createVoid(ArkdustCore.location("account"), Account.class);
    }

    public static class DataAttachment {
        public static final DeferredRegister<AttachmentType<?>> REGISTER = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, ArkdustCore.MODID);
        public static final DeferredHolder<AttachmentType<?>, AttachmentType<Account>> ATTACHMENT = REGISTER.register("account", () -> AttachmentType.builder(Account::new).serialize(CODEC).copyOnDeath().build());
    }

    public static class Requirement {
        private long lmb;
        private long originitePrime;
        private long originiumIngot;
        private long orundum;

        public Requirement() {
        }

        public Requirement(long lmb, long originitePrime, long originiumIngot, long orundum) {
            this.lmb = lmb;
            this.originitePrime = originitePrime;
            this.originiumIngot = originiumIngot;
            this.orundum = orundum;
        }

        public Requirement setLmb(long lmb) {
            this.lmb = lmb;
            return this;
        }

        public Requirement setOriginitePrime(long originitePrime) {
            this.originitePrime = originitePrime;
            return this;
        }

        public Requirement setOriginiumIngot(long originiumIngot) {
            this.originiumIngot = originiumIngot;
            return this;
        }

        public Requirement setOrundum(long orundum) {
            this.orundum = orundum;
            return this;
        }

        public long getLmb() {
            return lmb;
        }

        public long getOriginitePrime() {
            return originitePrime;
        }

        public long getOriginiumIngot() {
            return originiumIngot;
        }

        public long getOrundum() {
            return orundum;
        }

        public Requirement copy(){
            return new Requirement(this.lmb, this.originitePrime, this.originiumIngot, this.orundum);
        }

        public boolean isEmpty() {
            return this.lmb == 0 && this.originitePrime == 0 && this.originiumIngot == 0 && this.orundum == 0;
        }

        public Immut immutable(){
            return new Immut(this.lmb, this.originitePrime, this.originiumIngot, this.orundum);
        }

        public static class Immut extends Requirement {
            public Immut() {
            }

            public Immut(long lmb, long originitePrime, long originiumIngot, long orundum) {
                super(lmb, originitePrime, originiumIngot, orundum);
            }

            @Override
            @Deprecated(forRemoval = true)
            public Immut setOriginitePrime(long originitePrime) {
                return this;
            }

            @Override
            @Deprecated(forRemoval = true)
            public Immut setOriginiumIngot(long originiumIngot) {
                return this;
            }

            @Override
            @Deprecated(forRemoval = true)
            public Immut setOrundum(long orundum) {
                return this;
            }

            @Override
            @Deprecated(forRemoval = true)
            public Immut setLmb(long lmb) {
                return this;
            }
        }
    }

}

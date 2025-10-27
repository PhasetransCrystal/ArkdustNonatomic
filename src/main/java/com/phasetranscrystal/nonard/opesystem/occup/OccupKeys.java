package com.phasetranscrystal.nonard.opesystem.occup;

import net.minecraft.resources.ResourceLocation;

public class OccupKeys {

    public static final ResourceLocation CASTER = of("caster"),
            MEDIC = of("medic"),
            SNIPER = of("sniper"),
            GUARD = of("guard"),
            SUPPORTER = of("supporter"),
            VANGUARD = of("vanguard"),
            DEFENDER = of("defender"),
            SPECIALIST = of("specialist"),

            CASTER_PHALANX = of("caster/phalanx"),
            CASTER_MYSTIC = of("caster/mystic"),
            CASTER_MECH_ACCORD = of("caster/mech_accord"),
            CASTER_CHAIN = of("caster/chain"),
            CASTER_SPLASH = of("caster/splash"),
            CASTER_CORE = of("caster/core"),
            CASTER_BLAST = of("caster/blast"),

            MEDIC_MEDIC = of("medic/medic"),
            MEDIC_WANDERING = of("medic/wandering"),
            MEDIC_MULTI_TARGET = of("medic/multi_target"),
            MEDIC_INCANTATION = of("medic/incantation"),
            MEDIC_THERAPIST = of("medic/therapist"),
            MEDIC_CHAIN = of("medic/chain"),

            SNIPER_ARTILLERYMAN = of("sniper/artilleryman"),
            SNIPER_HUNTER = of("sniper/hunter"),
            SNIPER_FLINGER = of("sniper/flinger"),
            SNIPER_MARKSMAN = of("sniper/marksman"),
            SNIPER_DEADEYE = of("sniper/deadeye"),
            SNIPER_HEAVYSHOOTER = of("sniper/heavyshooter"),
            SNIPER_SPREADSHOOTER = of("sniper/spreadshooter"),
            SNIPER_BESIEGER = of("sniper/besieger"),

            GUARD_REAPER = of("guard/reaper"),
            GUARD_FIGHTER = of("guard/fighter"),
            GUARD_ARTS_FIGHTER = of("guard/arts_fighter"),
            GUARD_CENTURION = of("guard/centurion"),
            GUARD_LORD = of("guard/lord"),
            GUARD_INSTRUCTOR = of("guard/instructor"),
            GUARD_SWORDMASTER = of("guard/swordmaster"),
            GUARD_CRUSHER = of("guard/crusher"),
            GUARD_MUSHA = of("guard/musha"),
            GUARD_LIBERATOR = of("guard/liberator"),
            GUARD_DREADNOUGHT = of("guard/dreadnought"),

            SUPPORTER_ABJURER = of("supporter/abjurer"),
            SUPPORTER_ARTIFICER = of("supporter/artificer"),
            SUPPORTER_HEXER = of("supporter/hexer"),
            SUPPORTER_DECEL_BINDER = of("supporter/decel_binder"),
            SUPPORTER_SUMMONER = of("supporter/summoner"),
            SUPPORTER_RITUALIST = of("supporter/ritualist"),
            SUPPORTER_BARD = of("supporter/bard"),

            VANGUARD_PIONEER = of("vanguard/pioneer"),
            VANGUARD_AGENT = of("vanguard/agent"),
            VANGUARD_CHARGER = of("vanguard/charger"),
            VANGUARD_STANDARD_BEARER = of("vanguard/standard_bearer"),
            VANGUARD_TACTICIAN = of("vanguard/tactician"),

            DEFENDER_FORTRESS = of("defender/fortress"),
            DEFENDER_ARTS_PROTECTOR = of("defender/arts_protector"),
            DEFENDER_PROTECTOR = of("defender/protector"),
            DEFENDER_SENTINEL_PROTECTOR = of("defender/sentinel_protector"),
            DEFENDER_DUELIST = of("defender/duelist"),
            DEFENDER_JUGGERNAUT = of("defender/juggernaut"),
            DEFENDER_GUARDIAN = of("defender/guardian"),

            SPECIALIST_TRAPMASTER = of("specialist/trapmaster"),
            SPECIALIST_GEEK = of("specialist/geek"),
            SPECIALIST_MERCHANT = of("specialist/merchant"),
            SPECIALIST_AMBUSHER = of("specialist/ambusher"),
            SPECIALIST_HOOKMASTER = of("specialist/hookmaster"),
            SPECIALIST_EXECUTOR = of("specialist/executor"),
            SPECIALIST_DOLLKEEPER = of("specialist/dollkeeper"),
            SPECIALIST_PUSH_STROKER = of("specialist/push_stroker");

    private static ResourceLocation of(String path) {
        return ResourceLocation.fromNamespaceAndPath("arknights", path);
    }
}

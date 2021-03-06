package net.arcanamod.worldgen.trees.features;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import mcp.MethodsReturnNonnullByDefault;
import net.arcanamod.Arcana;
import net.arcanamod.ArcanaConfig;
import net.arcanamod.aspects.IAspectHandler;
import net.arcanamod.capabilities.AuraChunk;
import net.arcanamod.event.WorldTickHandler;
import net.arcanamod.mixin.TrunkPlacerTypeAccessor;
import net.arcanamod.world.Node;
import net.arcanamod.world.NodeType;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.IWorldGenerationBaseReader;
import net.minecraft.world.gen.IWorldGenerationReader;
import net.minecraft.world.gen.feature.BaseTreeFeatureConfig;
import net.minecraft.world.gen.feature.TreeFeature;
import net.minecraft.world.gen.foliageplacer.FoliagePlacer;
import net.minecraft.world.gen.trunkplacer.AbstractTrunkPlacer;
import net.minecraft.world.gen.trunkplacer.TrunkPlacerType;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;

import static java.util.Objects.requireNonNull;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class LargeOakTrunkPlacer extends AbstractTrunkPlacer{

    public static final Codec<LargeOakTrunkPlacer> CODEC = RecordCodecBuilder.create((builderInstance) -> getAbstractTrunkCodec(builderInstance).apply(builderInstance, LargeOakTrunkPlacer::new));
    public static final TrunkPlacerType<LargeOakTrunkPlacer> LARGE_OAK_PLACER = Registry.register(Registry.TRUNK_REPLACER, Arcana.arcLoc("large_oak_placer"), TrunkPlacerTypeAccessor.createTrunkPlacerType(CODEC));

    public LargeOakTrunkPlacer(int baseHeight, int heightRandA, int heightRandB){
        super(baseHeight, heightRandA, heightRandB);
    }

    protected TrunkPlacerType<?> getPlacerType(){
        return LARGE_OAK_PLACER;
    }

    public List<FoliagePlacer.Foliage> getFoliages(IWorldGenerationReader world, Random rand, int treeHeight, BlockPos pos, Set<BlockPos> logs, MutableBoundingBox box, BaseTreeFeatureConfig config){
        // todo: customisation options?
        int height = rand.nextInt(3) + rand.nextInt(3) + /*treeHeight*/ 12;
        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();
        if(y >= 1 && y + height + 1 < 255){
            BlockPos ground = pos.down();
            func_236909_a_(world, ground);
            func_236909_a_(world, ground.east());
            func_236909_a_(world, ground.south());
            func_236909_a_(world, ground.south().east());
            Set<BlockPos> leafNodes = new HashSet<>();
            // roots
            for(int x1 = -1; x1 <= 2; x1++){
                for(int z1 = -1; z1 <= 2; z1++){

                    // Get the root height by nesting random calls to make it biased towards 0
                    int rootHeight = rand.nextInt(rand.nextInt(2) + 2);

                    if(isRootOnEdge(x1) && isRootOnEdge(z1)){
                        rootHeight--; // Reduce on corners
                    }

                    if(rootHeight > 0){
                        BlockPos groundPos = new BlockPos(x + x1, y - 1, z + z1);

                        if(TreeFeature.isAirAt(world, groundPos)){
                            world.setBlockState(groundPos, Blocks.DIRT.getDefaultState(), 3);
                        }

                        // Place roots
                        for(int curHeight = 0; curHeight < rootHeight; curHeight++){
                            int curY = y + curHeight;
                            BlockPos curPos = new BlockPos(x + x1, curY, z + z1);

                            if(TreeFeature.isReplaceableAt(world, curPos)){
                                func_236911_a_(world, rand, curPos, logs, box, config);
                            }
                        }
                    }
                }
            }

            // main trunk
            Random ran = new Random();
            int switchnumber = ran.nextInt(3);
            for(int curHeight = 0; curHeight < height; ++curHeight){
                int curY = y + curHeight;
                BlockPos curPos = new BlockPos(x, curY, z);
                switch(switchnumber){
                    case 0:
                        if(TreeFeature.isReplaceableAt(world, curPos)){
                            func_236911_a_(world, rand, curPos, logs, box, config);
                            func_236911_a_(world, rand, curPos.east(), logs, box, config);
                            func_236911_a_(world, rand, curPos.south(), logs, box, config);
                            if(curHeight < 3){     // Prevent floating wood
                                func_236911_a_(world, rand, curPos.east().south(), logs, box, config);
                                BlockPos checker = new BlockPos(curPos.getX(), curPos.getY()-1,curPos.getZ());
                                if(TreeFeature.isAirAt(world, checker)){
                                    func_236911_a_(world, rand, checker, logs, box, config);
                                }
                                BlockPos checkerE = new BlockPos(curPos.east().getX(), curPos.east().getY()-1,curPos.east().getZ());
                                if(TreeFeature.isAirAt(world, checkerE)){
                                    func_236911_a_(world, rand, checkerE, logs, box, config);
                                }
                                BlockPos checkerS = new BlockPos(curPos.south().getX(), curPos.south().getY()-1,curPos.south().getZ());
                                if(TreeFeature.isAirAt(world, checkerS)){
                                    func_236911_a_(world, rand, checkerS, logs, box, config);
                                }
                                BlockPos checkerES = new BlockPos(curPos.east().south().getX(), curPos.east().south().getY()-1,curPos.east().south().getZ());
                                if(TreeFeature.isAirAt(world, checkerES)){
                                    func_236911_a_(world, rand, checkerES, logs, box, config);
                                }
                                break;
                        }

                    }

                    case 1:
                        if(TreeFeature.isReplaceableAt(world, curPos)){
                        func_236911_a_(world, rand, curPos, logs, box, config);
                        func_236911_a_(world, rand, curPos.east(), logs, box, config);
                        func_236911_a_(world, rand, curPos.east().south(), logs, box, config);
                        if(curHeight < 3){ // Prevent floating wood
                            func_236911_a_(world, rand, curPos.south(), logs, box, config);
                            BlockPos checker = new BlockPos(curPos.getX(), curPos.getY()-1,curPos.getZ());
                            if(TreeFeature.isAirAt(world, checker)){
                                func_236911_a_(world, rand, checker, logs, box, config);
                            }
                            BlockPos checkerE = new BlockPos(curPos.east().getX(), curPos.east().getY()-1,curPos.east().getZ());
                            if(TreeFeature.isAirAt(world, checkerE)){
                                func_236911_a_(world, rand, checkerE, logs, box, config);
                            }
                            BlockPos checkerS = new BlockPos(curPos.south().getX(), curPos.south().getY()-1,curPos.south().getZ());
                            if(TreeFeature.isAirAt(world, checkerS)){
                                func_236911_a_(world, rand, checkerS, logs, box, config);
                            }
                            BlockPos checkerES = new BlockPos(curPos.east().south().getX(), curPos.east().south().getY()-1,curPos.east().south().getZ());
                            if(TreeFeature.isAirAt(world, checkerES)){
                                func_236911_a_(world, rand, checkerES, logs, box, config);
                            }
                        }
                        break;
                        }
                    case 2:
                        if(TreeFeature.isReplaceableAt(world, curPos)){
                        func_236911_a_(world, rand, curPos, logs, box, config);
                        func_236911_a_(world, rand, curPos.east().south(), logs, box, config);
                        func_236911_a_(world, rand, curPos.south(), logs, box, config);
                        if(curHeight < 3){ // Prevent floating wood
                            func_236911_a_(world, rand, curPos.east(), logs, box, config);
                            BlockPos checker = new BlockPos(curPos.getX(), curPos.getY()-1,curPos.getZ());
                            if(TreeFeature.isAirAt(world, checker)){
                                func_236911_a_(world, rand, checker, logs, box, config);
                            }
                            BlockPos checkerE = new BlockPos(curPos.east().getX(), curPos.east().getY()-1,curPos.east().getZ());
                            if(TreeFeature.isAirAt(world, checkerE)){
                                func_236911_a_(world, rand, checkerE, logs, box, config);
                            }
                            BlockPos checkerS = new BlockPos(curPos.south().getX(), curPos.south().getY()-1,curPos.south().getZ());
                            if(TreeFeature.isAirAt(world, checkerS)){
                                func_236911_a_(world, rand, checkerS, logs, box, config);
                            }
                            BlockPos checkerES = new BlockPos(curPos.east().south().getX(), curPos.east().south().getY()-1,curPos.east().south().getZ());
                            if(TreeFeature.isAirAt(world, checkerES)){
                                func_236911_a_(world, rand, checkerES, logs, box, config);
                            }
                        }
                        break;
                        }
                }

                // Branches
                if(curHeight > 5 && curHeight < height - 4){
                    int branchCount = 1 + (curHeight / 3);
                    double offset = Math.PI * 2 * rand.nextDouble();

                    // Make fewer branches at the bottom, but more at the top
                    for(int i = 0; i < branchCount; i++){
                        double angle = (((double)i / branchCount) * (Math.PI * 2)) + offset + (rand.nextDouble() * 0.2);
                        int length = rand.nextInt(2) + (3 - branchCount) + curHeight / 2;
                        // Choose a starting location on the trunk
                        BlockPos start = chooseStart(curPos);

                        for(int j = 0; j <= length; j++){
                            // Traverse through the branch
                            BlockPos local = start.add(Math.cos(angle) * j, j / 2.0, Math.sin(angle) * j);

                            // Place logs if it's air
                            if(TreeFeature.isAirAt(world, local)){
                                func_236911_a_(world, rand, local, logs, box, config);
                            }

                            // If we're at the end, mark this position for generating leaves
                            if(j == length){
                                leafNodes.add(local);
                            }
                        }
                    }

                }

                // Add leaves to the top of the trunk
                if(curHeight == height - 1){
                    leafNodes.add(curPos);
                    leafNodes.add(curPos.east());
                    leafNodes.add(curPos.south());
                    leafNodes.add(curPos.east().south());

                }
            }

            List<FoliagePlacer.Foliage> list = Lists.newArrayList();
            for(BlockPos node : leafNodes)
                // position, ???, ???
                list.add(new FoliagePlacer.Foliage(node, 1, false));
            return list;
        }
        return Collections.emptyList();
    }

    private boolean isRootOnEdge(int axis){
        return axis == -1 || axis == 2;
    }

    private static BlockPos chooseStart(BlockPos start){
        return start;
    }

    private boolean isSpaceClearForHeight(IWorldGenerationBaseReader world, BlockPos pos, int height){
        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();
        BlockPos.Mutable cursor = new BlockPos.Mutable();

        for(int i = 0; i <= height + 1; ++i){
            int r = 1;
            if(i == 0)
                r = 0;

            if(i >= height - 1)
                r = 2;

            for(int xr = -r; xr <= r; ++xr)
                for(int zr = -r; zr <= r; ++zr)
                    if(!TreeFeature.isReplaceableAt(world, cursor.setPos(x + xr, y + i, z + zr)))
                        return false;
        }

        return true;
    }
}
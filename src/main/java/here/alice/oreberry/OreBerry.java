package here.alice.oreberry;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import net.minecraft.block.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.random.RandomGenerator;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraft.world.event.GameEvent;

import java.util.Map;

public class OreBerry extends PlantBlock {
	public static final DirectionProperty FACING = HorizontalFacingBlock.FACING;
	private static final Map<Direction, VoxelShape> BOUNDING_SHAPES_SMALL = Maps.newEnumMap(ImmutableMap.of(
			Direction.UP,    Block.createCuboidShape(2,  0, 2,  14, 12, 14),
			Direction.DOWN,  Block.createCuboidShape(2,  4, 2,  14, 16, 14),
			Direction.NORTH, Block.createCuboidShape(2,  2, 14, 14, 14, 16),
			Direction.SOUTH, Block.createCuboidShape(2,  2, 0,  14, 14, 2 ),
			Direction.WEST,  Block.createCuboidShape(14, 2, 2,  16, 14, 14),
			Direction.EAST,  Block.createCuboidShape(0,  2, 2,  2,  14, 14)
	));
	private static final Map<Direction, VoxelShape> BOUNDING_SHAPES_LARGE = Maps.newEnumMap(ImmutableMap.of(
			Direction.UP,    Block.createCuboidShape(1, 0, 1, 15, 14, 15),
			Direction.DOWN,  Block.createCuboidShape(1, 2, 1, 15, 16, 15),
			Direction.NORTH, Block.createCuboidShape(1, 1, 2, 15, 15, 16),
			Direction.SOUTH, Block.createCuboidShape(1, 1, 0, 15, 15, 14),
			Direction.WEST,  Block.createCuboidShape(2, 1, 1, 16, 15, 15),
			Direction.EAST,  Block.createCuboidShape(0, 1, 1, 14, 15, 15)
	));
	
	private static Placement placementTempVar;
	private static IntProperty ageTemp;
	private static Integer maxAgeTemp;

	private final String type;
	public final Placement placement;
	private final IntProperty age;
	private final Integer maxAge;
	private final Identifier berryId;
	private final Identifier bushId;
	
	public static OreBerry create(String type, Placement placement, int maxAge){
		placementTempVar = placement;
		ageTemp = IntProperty.of("age", 0, maxAge);
		maxAgeTemp = maxAge;
		return new OreBerry(type);
	}

	public OreBerry(String type) {
		super(Settings.of(Material.PLANT).ticksRandomly().noCollision().sounds(BlockSoundGroup.SWEET_BERRY_BUSH));
		this.type = type;
		this.placement = placementTempVar;
		this.age = ageTemp;
		this.maxAge = maxAgeTemp;
		this.berryId = Initializer.identifier(type + "_berries");
		this.bushId = Initializer.identifier(type + "_berry_bush");
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(ageTemp);
		if(placementTempVar == Placement.SIDE) {
			builder.add(FACING);
		}
	}

	@Override
	public ItemStack getPickStack(BlockView world, BlockPos pos, BlockState state) {
		return new ItemStack(Registry.ITEM.get(bushId));
	}

	protected boolean canPlantOnTop(BlockState floor, BlockView world, BlockPos pos) {
		return !"sculk".equals(type)? floor.isIn(BlockTags.BASE_STONE_OVERWORLD) : floor.isOf(Blocks.SCULK);
	}

	@Override
	public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
		BlockPos blockPos = pos.offset((placement.equals(Placement.SIDE)? state.get(FACING) : placement.orientation).getOpposite());
		return this.canPlantOnTop(world.getBlockState(blockPos), world, blockPos);
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		Direction offset = placement.equals(Placement.SIDE)? state.get(FACING) : placement.orientation;
		if (state.get(age) == 0) {
			return BOUNDING_SHAPES_SMALL.get(offset);
		} else {
			return BOUNDING_SHAPES_LARGE.get(offset);
		}
	}

	@Override
	public boolean hasRandomTicks(BlockState state) {
		return state.get(age) < maxAge;
	}

	@Override
	public void randomTick(BlockState state, ServerWorld world, BlockPos pos, RandomGenerator random) {
		int i = state.get(age);
		if (i < maxAge && random.nextInt(5) == 0 && world.getBaseLightLevel(pos, 0) >= 5) {
			BlockState blockState = state.with(age, i + 1);
			world.setBlockState(pos, blockState, Block.NOTIFY_LISTENERS);
			world.emitGameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.create(blockState));
		}
	}

	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		int i = state.get(age);
		boolean bl = true;
		if (i > 0) {
			int j = 1 + (i > 1? world.random.nextInt(i - 1) : 0);
			dropStack(world, pos, new ItemStack(Registry.ITEM.get(berryId), j));
			world.playSound(null, pos, SoundEvents.BLOCK_SWEET_BERRY_BUSH_PICK_BERRIES, SoundCategory.BLOCKS, 1.0F, 0.8F + world.random.nextFloat() * 0.4F);
			BlockState blockState = state.with(age, 0);
			world.setBlockState(pos, blockState, Block.NOTIFY_LISTENERS);
			world.emitGameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.create(player, blockState));
			return ActionResult.success(world.isClient);
		} else {
			return super.onUse(state, world, pos, player, hand, hit);
		}
	}

	@Override
	public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
		if (entity instanceof LivingEntity && entity.getType() != EntityType.BAT && state.getOutlineShape(world, pos).getBoundingBox().offset(pos).intersects(entity.getBoundingBox())) {
			entity.slowMovement(state, new Vec3d(0.8F, 0.75, 0.8F));
			if (!world.isClient && state.get(age) > 0 && (entity.lastRenderX != entity.getX() || entity.lastRenderZ != entity.getZ())) {
				double d = Math.abs(entity.getX() - entity.lastRenderX);
				double e = Math.abs(entity.getZ() - entity.lastRenderZ);
				if (d >= 0.003F || e >= 0.003F) {
					entity.damage(DamageSource.SWEET_BERRY_BUSH, 1.0F);
				}
			}
		}
	}
	
	public String getType() {
		return type;
	}

	public enum Placement implements StringIdentifiable {
		FLOOR(Direction.UP),
		CEILING(Direction.DOWN),
		SIDE(Direction.NORTH);

		private final Direction orientation;

		Placement(Direction def){
			this.orientation = def;
		}

		@Override
		public String asString() {
			return this.name().toLowerCase();
		}
	}
}

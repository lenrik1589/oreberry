package here.alice.oreberry;

import net.minecraft.block.BlockState;
import net.minecraft.item.AliasedBlockItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.util.math.Direction;

public class OreBerryBushItem extends AliasedBlockItem {
	private final OreBerry berry;
	public OreBerryBushItem(OreBerry oreBerry, Settings settings) {
		super(oreBerry, settings);
		berry = oreBerry;
	}
	
	@Override
	protected BlockState getPlacementState(ItemPlacementContext context){
		if(!OreBerry.Placement.SIDE.equals(berry.placement))
			return super.getPlacementState(context);
		BlockState blockState = this.getBlock().getPlacementState(context);
		if(blockState == null)
			return null;
		if(Direction.Type.HORIZONTAL.test(context.getSide())) {
			blockState = blockState.with(OreBerry.FACING, context.getSide());
			if (this.canPlace(context, blockState)) return blockState;
		}
		for(Direction direction : Direction.Type.HORIZONTAL){
			if(direction.equals(context.getSide())) continue;
			blockState = blockState.with(OreBerry.FACING, direction);
			if(this.canPlace(context, blockState)) return blockState;
		}
		return null;
	}
}

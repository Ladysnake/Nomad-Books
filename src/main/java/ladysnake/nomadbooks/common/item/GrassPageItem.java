package ladysnake.nomadbooks.common.item;

import ladysnake.nomadbooks.NomadBooks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class GrassPageItem extends Item {
    public GrassPageItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        user.getStackInHand(hand).decrement(1);
        boolean canInsert = user.inventory.insertStack(new ItemStack(NomadBooks.NOMAD_PAGE));
        if (!canInsert) {
            user.dropStack(new ItemStack(NomadBooks.NOMAD_PAGE));
        }
        return TypedActionResult.consume(user.getStackInHand(hand));
    }
}

package mekanism.client.render.bolt;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import org.apache.commons.lang3.tuple.Pair;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import mekanism.client.render.bolt.BoltEffect.BoltQuads;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Matrix4f;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.math.Vec3d;

public class BoltRenderer {
    /** Amount of times per tick we refresh. 3 implies 60 Hz. */
    private static final float REFRESH_TIME = 3F;

    private Random random = new Random();

    private Map<Object, Set<BoltInstance>> boltRenderMap = new Object2ObjectOpenHashMap<>();

    private final int newBoltRate;
    private final int boltLifespan;

    private final BoltEffect boltEffect;
    private final FadeFunction fadeFunction;

    private float refreshTimestamp;
    private int renderTime;

    public static BoltRenderer create(BoltEffect boltEffect) {
        return create(boltEffect, 30, 60, FadeFunction.fade(0.25F));
    }

    public static BoltRenderer create(BoltEffect boltEffect, int boltLifespan, int newBoltRate, FadeFunction fadeFunction) {
        return new BoltRenderer(boltEffect, boltLifespan, newBoltRate, fadeFunction);
    }

    protected BoltRenderer(BoltEffect boltEffect, int boltLifespan, int newBoltRate, FadeFunction fadeFunction) {
        this.boltEffect = boltEffect;
        this.boltLifespan = boltLifespan;
        this.newBoltRate = newBoltRate;
        this.fadeFunction = fadeFunction;
    }

    public void render(Object renderer, Vec3d start, Vec3d end, int count, int segments, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn) {
        IVertexBuilder buffer = bufferIn.getBuffer(RenderType.getLightning());
        Matrix4f matrix = matrixStackIn.getLast().getMatrix();

        Set<BoltInstance> bolts = boltRenderMap.computeIfAbsent(renderer, (r) -> new HashSet<>());

        if (partialTicks < refreshTimestamp)
            partialTicks += 1;

        if (partialTicks - refreshTimestamp >= (1 / REFRESH_TIME)) {
            if (renderTime == 0) {
                renderTime = random.nextInt(newBoltRate) + 1;
                List<BoltQuads> quads = boltEffect.generate(start, end, count, segments);
                bolts.add(new BoltInstance(boltLifespan, quads));
            } else {
                renderTime--;
            }

            bolts.removeIf(bolt -> bolt.tick());
            if (bolts.isEmpty()) {
                boltRenderMap.remove(renderer);
            }

            refreshTimestamp = partialTicks % 1;
        }

        bolts.forEach(bolt -> bolt.render(matrix, buffer));
    }

    public class BoltInstance {

        private List<BoltQuads> renderQuads = new ArrayList<>();
        private final float lifespan;
        private int ticksExisted;

        public BoltInstance(float lifespan, List<BoltQuads> renderQuads) {
            this.lifespan = lifespan;
            this.renderQuads = renderQuads;
        }

        public void render(Matrix4f matrix, IVertexBuilder buffer) {
            float lifeScale = ticksExisted / lifespan;
            Pair<Integer, Integer> bounds = fadeFunction.getRenderBounds(renderQuads.size(), lifeScale);
            for (int i = bounds.getLeft(); i < bounds.getRight(); i++) {
                renderQuads.get(i).render(matrix, buffer, 1);
            }
        }

        public boolean tick() {
            return ticksExisted++ >= lifespan;
        }
    }

    public interface FadeFunction {

        public static FadeFunction NONE = (totalBolts, lifeScale) -> Pair.of(0, totalBolts);

        public static FadeFunction fade(float fade) {
            return (totalBolts, lifeScale) -> {
                int start = lifeScale > (1 - fade) ? (int) (totalBolts * (lifeScale - (1 - fade)) / fade) : 0;
                int end = lifeScale < fade ? (int) (totalBolts * (lifeScale / fade)) : totalBolts;
                return Pair.of(start, end);
            };
        }

        Pair<Integer, Integer> getRenderBounds(int totalBolts, float lifeScale);
    }
}
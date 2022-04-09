package app.ecs.components;

import app.audio.SoundEffect;
import app.ecs.Entity;
import app.math.components.OLTransform;
import app.renderer.Textures;
import app.renderer.debug.billboards.Billboards;
import app.renderer.draw.EditorRenderer;
import app.utilities.OpenFileDialog;
import imgui.ImGui;

import java.io.File;
import java.nio.file.Paths;

public class SoundEffectComponent extends Component {
    private String path = "";
    private String prePath = "";
    private File file;

    private final SoundEffect soundEffect;
    private final OLTransform olTransform;

    private final int playIcon;
    private final int stopIcon;
    private final int pauseIcon;

    public SoundEffectComponent(Entity ownerEntity) {
        super(ownerEntity);
        file = new File("");

        Textures textures = EditorRenderer.getTextures();
        playIcon = textures.loadTexture("src\\main\\resources\\editor\\icons\\audio\\play.png");
        stopIcon = textures.loadTexture("src\\main\\resources\\editor\\icons\\audio\\stop.png");
        pauseIcon = textures.loadTexture("src\\main\\resources\\editor\\icons\\audio\\pause.png");
        Billboards billboards = new Billboards(EditorRenderer.getOpenGLObjects(), textures.loadTexture("src\\main\\resources\\editor\\icons\\audio\\audio.png"));
        soundEffect = new SoundEffect(billboards);

        olTransform = ownerEntity.getComponent(TransformComponent.class).getOlTransform();
    }

    @Override
    public void update(float dt) {
        soundEffect.setPosition(olTransform.getPosition());
        soundEffect.setVelocity(olTransform.getRotation());
    }

    @Override
    public void imguiDraw() {
        if (ImGui.button("Music"))
            path = OpenFileDialog.openFile("ogg").orElse(prePath);

        if (!path.isEmpty() && !prePath.equals(path)) {
            prePath = path;
            file = new File(path);
            soundEffect.loadSound(Paths.get(path));
        }
        ImGui.sameLine();
        ImGui.textWrapped(file.getName());

        ImGui.pushID("frame");
        int[] frameValue = {soundEffect.getFrame()};
        ImGui.sliderInt("##Y", frameValue, 0, soundEffect.getTotalFrame(), "%d%%");
        ImGui.popID();

        ImGui.columns(3, "", false);

        ImGui.pushID("play");
        if (ImGui.imageButton(playIcon, 32, 32))
            soundEffect.play();
        ImGui.popID();
        ImGui.nextColumn();

        ImGui.pushID("pause");
        if (ImGui.imageButton(pauseIcon, 32, 32))
            soundEffect.pause();
        ImGui.popID();
        ImGui.nextColumn();

        ImGui.pushID("stop");
        if (ImGui.imageButton(stopIcon, 32, 32))
            soundEffect.stop();
        ImGui.popID();

        ImGui.columns(1);

        ImGui.pushID("volume");
        if (ImGui.button("Volume"))
            soundEffect.setVolume(0f);
        ImGui.sameLine();
        float[] volumeValue = {soundEffect.getVolume()};
        ImGui.dragFloat("##Y", volumeValue, 0.1f, 0f, 100f);
        soundEffect.setVolume(volumeValue[0]);
        ImGui.popID();

    }

    @Override
    public void cleanUp() {
        soundEffect.delete();
    }

    @Override
    public int getComponentType() {
        return 0;
    }

}

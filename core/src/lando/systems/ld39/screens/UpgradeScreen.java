package lando.systems.ld39.screens;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntIntMap;
import lando.systems.ld39.LudumDare39;
import lando.systems.ld39.objects.Item;
import lando.systems.ld39.objects.PlayerCar;
import lando.systems.ld39.ui.Button;
import lando.systems.ld39.utils.Assets;

public class UpgradeScreen extends BaseScreen {

    Rectangle buttonHeaderRegion;
    Rectangle buttonRegion;

    Rectangle infoHeaderRegion;
    Rectangle infoRegion;

    Rectangle carRegion;

    Vector3 touchPos;
    Button continueButton;
    Button purchaseButton;

    public class UpgradeItem {
        public int type;
        public String description;
        public TextureRegion buttonTexture;
        public TextureRegion upgradeTexture;
        public Button button;
        public int currentLevel;
        public int maxLevel;
    }
    Array<UpgradeItem> upgradeItems;
    UpgradeItem selectedUpgrade;

    IntIntMap currentUpgrades;
    PlayerCar playerCar;

    // TODO: temporary
    int cost = 1000;

    public UpgradeScreen(IntIntMap currentUpgrades) {
        // reset damage
        currentUpgrades.put(Item.Damage, 0);

        this.currentUpgrades = currentUpgrades;
        playerCar = new PlayerCar(null);

        buttonHeaderRegion = new Rectangle(40, 450, 200, 60);
        buttonRegion = new Rectangle(40, 90, 200, 350);

        infoHeaderRegion = new Rectangle(300, 450, 200, 60);
        infoRegion   = new Rectangle(301, 90, 200, 350);

        carRegion    = new Rectangle(561, 93, 200, 415);

        initializeUpgradeItems();

        touchPos = new Vector3();

        final float continue_width = carRegion.width;
        final float continue_height = buttonRegion.y - 35f;
        Rectangle continueButtonRect = new Rectangle(carRegion.x, 10f, continue_width, continue_height);
        continueButton = new Button(Assets.defaultNinePatch, continueButtonRect, hudCamera, "Hit the road", null);
        continueButton.textColor = Color.BLACK;

        final float confirm_button_y = infoRegion.y + 10f;
        final float confirm_button_width = infoRegion.width / 2f;
        final float confirm_button_height = 50f;
        final float confirm_button_x = infoRegion.x + ((infoRegion.width / 2f) - (confirm_button_width / 2f));
        Rectangle confirmButtonRect = new Rectangle(confirm_button_x, confirm_button_y, confirm_button_width, confirm_button_height);
        purchaseButton = new Button(Assets.defaultNinePatch, confirmButtonRect, hudCamera, "Purchase", null);
        purchaseButton.textColor = Color.BLACK;

        alpha.setValue(1f);
        Tween.to(alpha, 1, 1)
                .target(0)
                .start(Assets.tween);
    }

    @Override
    public void update(float dt) {
        if (Gdx.input.justTouched()) {
            hudCamera.unproject(touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0f));

            int touchX = (int) touchPos.x;
            int touchY = (int) hudCamera.viewportHeight - (int) touchPos.y;
            for (UpgradeItem upgradeItem : upgradeItems) {
                upgradeItem.button.update(dt);
                if (upgradeItem.button.checkForTouch(touchX, touchY)) {
                    selectedUpgrade = upgradeItem;

                    // TODO: set properly
                    cost = (int) (Math.random() * 501f) + 500;
                }
            }

            if (continueButton.checkForTouch(touchX, touchY)) {
                Tween.to(alpha, 1, 1)
                        .target(1)
                        .setCallback(new TweenCallback() {
                            @Override
                            public void onEvent(int i, BaseTween<?> baseTween) {
                                LudumDare39.game.setScreen(new GameScreen(currentUpgrades));
                            }
                        })
                        .start(Assets.tween);
            }

            // added 2 null checks - if you don't select anything these are null.
            if (selectedUpgrade != null && selectedUpgrade.currentLevel != selectedUpgrade.maxLevel && purchaseButton != null &&  purchaseButton.checkForTouch(touchX, touchY)) {
                handleUpgrade();
            }
        }
        hudCamera.update();

        playerCar.animStateTime += dt;
        playerCar.setUpgrades(currentUpgrades);
        playerCar.setSize(carRegion.width, carRegion.height);
        playerCar.setBoundsLocation(carRegion.x, carRegion.y);
    }

    @Override
    public void render(SpriteBatch batch) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.setProjectionMatrix(hudCamera.combined);
        batch.begin();
        {
            // Draw screen background
            batch.draw(Assets.garageBackground, 0, 0, hudCamera.viewportWidth, hudCamera.viewportHeight);

            // Draw section backgrounds
            batch.setColor(64f / 255f, 64f / 255f, 64f / 255f, 0.9f);
            batch.draw(Assets.whitePixel, buttonRegion.x, buttonRegion.y, buttonRegion.width, buttonRegion.height);
            batch.setColor(64f / 255f, 64f / 255f, 64f / 255f, 0.9f);
            batch.draw(Assets.whitePixel, infoRegion.x, infoRegion.y, infoRegion.width, infoRegion.height);
            batch.setColor(64f / 255f, 64f / 255f, 64f / 255f, 0.95f);
            batch.draw(Assets.whitePixel, carRegion.x, carRegion.y, carRegion.width, carRegion.height);
            batch.setColor(Color.WHITE);

            // Draw header text
            final float textHeight = 20f;
            Assets.drawString(batch, "System Upgrades",
                    buttonHeaderRegion.x, buttonHeaderRegion.y + buttonHeaderRegion.height - textHeight,
                    Color.BLACK, 0.4f, Assets.font, buttonHeaderRegion.width, Align.center);
            Assets.drawString(batch, "Upgrade Info",
                    infoHeaderRegion.x, infoHeaderRegion.y + infoHeaderRegion.height - textHeight,
                    Color.BLACK, 0.4f, Assets.font, infoHeaderRegion.width, Align.center);

            // Draw upgrade item buttons
            final float button_icon_padding = 10f;
            for (UpgradeItem upgradeItem : upgradeItems) {
                batch.draw(upgradeItem.buttonTexture,
                        upgradeItem.button.bounds.x + button_icon_padding,
                        upgradeItem.button.bounds.y + button_icon_padding,
                        upgradeItem.button.bounds.width - 2f * button_icon_padding,
                        upgradeItem.button.bounds.height - 2f * button_icon_padding);
                upgradeItem.button.render(batch);
            }

            // Draw info section details
            if (selectedUpgrade != null) {
                selectedUpgrade.currentLevel = currentUpgrades.get(selectedUpgrade.type, 0);

                final float margin_top = 10f;
                final float margin_left = 10f;
                final float icon_size = 64f;
                final float icon_pos_x = infoRegion.x + (infoRegion.width / 2f) - (icon_size / 2f);
                final float icon_pos_y = infoRegion.y + infoRegion.height - icon_size - margin_top;
                batch.draw(selectedUpgrade.buttonTexture, icon_pos_x, icon_pos_y, icon_size, icon_size);

                final float type_pos_y = icon_pos_y - textHeight;
                Assets.drawString(batch, Item.getName(selectedUpgrade.type),
                        infoRegion.x, icon_pos_y - textHeight,
                        Color.GOLD, 0.5f, Assets.font, infoRegion.width, Align.center);

                final float description_pos_y = type_pos_y - 1.5f * margin_top - textHeight;
                Assets.drawString(batch, selectedUpgrade.description,
                        infoRegion.x + margin_left, description_pos_y,
                        Color.WHITE, 0.35f, Assets.font, infoRegion.width, Align.left);

                final float upgrade_height = 20f;

                // TODO: determine cost text based on upgrade level
                final float cost_pos_y = purchaseButton.bounds.y + purchaseButton.bounds.height + 2f * margin_top + upgrade_height + 2f * textHeight;
                Assets.drawString(batch, "$" + cost, infoRegion.x, cost_pos_y,
                        Color.GOLDENROD, 0.55f, Assets.font, infoRegion.width, Align.center);

                // current upgrade level / max upgrade levels
                final float upgrade_level_pos_y = purchaseButton.bounds.y + purchaseButton.bounds.height + margin_top;
                final float max_upgrade_width = infoRegion.width - 2f * margin_left;
                final float current_upgrade_width = ((float) selectedUpgrade.currentLevel / (float) selectedUpgrade.maxLevel) * max_upgrade_width;
                final float next_upgrade_width = MathUtils.clamp((((float) selectedUpgrade.currentLevel + 1f) / (float) selectedUpgrade.maxLevel), 0f, 1f) * max_upgrade_width;
                batch.draw(Assets.whitePixel, infoRegion.x + margin_left, upgrade_level_pos_y, max_upgrade_width, upgrade_height);
                batch.setColor(Color.YELLOW);
                batch.draw(Assets.whitePixel, infoRegion.x + margin_left, upgrade_level_pos_y, next_upgrade_width, upgrade_height);
                batch.setColor(Color.GREEN);
                batch.draw(Assets.whitePixel, infoRegion.x + margin_left, upgrade_level_pos_y, current_upgrade_width, upgrade_height);
                batch.setColor(Color.WHITE);
                Assets.defaultNinePatch.draw(batch, infoRegion.x + margin_left, upgrade_level_pos_y, max_upgrade_width, upgrade_height);

                // purchase upgrade button
                batch.setColor((selectedUpgrade.currentLevel == selectedUpgrade.maxLevel) ? Color.DARK_GRAY : Color.WHITE);
                batch.draw(Assets.whitePixel, purchaseButton.bounds.x, purchaseButton.bounds.y, purchaseButton.bounds.width, purchaseButton.bounds.height);
                purchaseButton.render(batch);
                batch.setColor(Color.WHITE);
            }

            playerCar.render(batch);

            batch.draw(Assets.whitePixel, continueButton.bounds.x, continueButton.bounds.y, continueButton.bounds.width, continueButton.bounds.height);
            continueButton.render(batch);

            // Screen transition overlay
            batch.setColor(0, 0, 0, alpha.floatValue());
            batch.draw(Assets.whitePixel, 0, 0, hudCamera.viewportWidth, hudCamera.viewportHeight);
            batch.setColor(Color.WHITE);
        }
        batch.end();
    }

    private void initializeUpgradeItems() {
        UpgradeItem upgradeBattery = new UpgradeItem();
        upgradeBattery.type = Item.Battery;
        upgradeBattery.description = "Increased travel distance on one charge";
        upgradeBattery.buttonTexture = Assets.upgradeIconBattery;
        upgradeBattery.upgradeTexture = Assets.carBase;
        upgradeBattery.currentLevel = currentUpgrades.get(Item.Battery, 0);
        upgradeBattery.maxLevel = Item.getMaxLevel(Item.Battery);

        UpgradeItem upgradeMotor = new UpgradeItem();
        upgradeMotor.type = Item.Engine;
        upgradeMotor.description = "Increased overall top speed";
        upgradeMotor.buttonTexture = Assets.upgradeIconMotor;
        upgradeMotor.upgradeTexture = Assets.carBase;
        upgradeMotor.currentLevel = currentUpgrades.get(Item.Engine, 0);
        upgradeMotor.maxLevel = Item.getMaxLevel(Item.Engine);

        UpgradeItem upgradeBooster = new UpgradeItem();
        upgradeBooster.type = Item.Booster;
        upgradeBooster.description = "Increased number of turbo boosts";
        upgradeBooster.buttonTexture = Assets.upgradeIconBooster;
        upgradeBooster.upgradeTexture = Assets.carBase;
        upgradeBooster.currentLevel = currentUpgrades.get(Item.Booster, 0);
        upgradeBooster.maxLevel = Item.getMaxLevel(Item.Booster);

        UpgradeItem upgradeWeapon = new UpgradeItem();
        upgradeWeapon.type = Item.Weapons;
        upgradeWeapon.description = "Improved weapon system";
        upgradeWeapon.buttonTexture = Assets.upgradeIconWeapon;
        upgradeWeapon.upgradeTexture = Assets.carBase;
        upgradeWeapon.currentLevel = currentUpgrades.get(Item.Weapons, 0);
        upgradeWeapon.maxLevel = Item.getMaxLevel(Item.Weapons);

        UpgradeItem upgradeHull = new UpgradeItem();
        upgradeHull.type = Item.Chassis;
        upgradeHull.description = "Increased damage resistance";
        upgradeHull.buttonTexture = Assets.upgradeIconHull;
        upgradeHull.upgradeTexture = Assets.carBase;
        upgradeHull.currentLevel = currentUpgrades.get(Item.Chassis, 0);
        upgradeHull.maxLevel = Item.getMaxLevel(Item.Chassis);

        UpgradeItem upgradeTires = new UpgradeItem();
        upgradeTires.type = Item.Wheels;
        upgradeTires.description = "Improved handling";
        upgradeTires.buttonTexture = Assets.upgradeIconTire;
        upgradeTires.upgradeTexture = Assets.carBase;
        upgradeTires.currentLevel = currentUpgrades.get(Item.Weapons, 0);
        upgradeTires.maxLevel = Item.getMaxLevel(Item.Wheels);

        upgradeItems = new Array<UpgradeItem>();
        upgradeItems.addAll(upgradeBattery, upgradeMotor, upgradeBooster, upgradeWeapon, upgradeHull, upgradeTires);

        final float button_margin_left = 10f;
        final float button_margin_bottom = 20f;
        final float button_spacing_x = 20f;
        final float button_size = (buttonRegion.width - 2f * button_margin_left - button_spacing_x) / 2f;
        final float button_spacing_y = (buttonRegion.height - 2f * button_margin_bottom - 3f * button_size) / 2f;

        for (int i = 0; i < upgradeItems.size; i += 2) {
            UpgradeItem item1 = upgradeItems.get(i);
            Rectangle bounds1 = new Rectangle(
                    buttonRegion.x + button_margin_left,
                    buttonRegion.y + button_margin_bottom + ((i / 2) * button_size) + ((i / 2) * button_spacing_y),
                    button_size, button_size
            );
            item1.button = new Button(Assets.defaultNinePatch, bounds1, hudCamera);
            item1.button.textColor = Color.BLACK;

            UpgradeItem item2 = upgradeItems.get(i+1);
            Rectangle bounds2 = new Rectangle(
                    buttonRegion.x + button_margin_left + button_size + button_spacing_x,
                    buttonRegion.y + button_margin_bottom + ((i / 2) * button_size) + ((i / 2) * button_spacing_y),
                    button_size, button_size
            );
            item2.button = new Button(Assets.defaultNinePatch, bounds2, hudCamera);
            item2.button.textColor = Color.BLACK;
        }

        selectedUpgrade = null;
    }

    private void handleUpgrade() {
        if (selectedUpgrade == null) return;

        int level = currentUpgrades.get(selectedUpgrade.type, 0);
        int max = Item.getMaxLevel(selectedUpgrade.type);
        if (max != -1) {
            // don't set level of an item that doesn't exist
            currentUpgrades.put(selectedUpgrade.type, MathUtils.clamp(level + 1, 0, max));
        }
    }

}

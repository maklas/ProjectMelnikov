package ru.maklas.melnikov.utils;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Queue;

public class WangTile {

    private TextureRegion[] regions;
    private int size;
    private Queue<Queue<Integer>> rows = new Queue<>();
    private int left;
    private int bot;

    public WangTile(Texture texture) {
        size = texture.getWidth() / 4;
        regions = new TextureRegion[16];
        int count = 0;
        for (int i = 1; i <= 4; i++){
            for (int j = 1; j <= 4; j++) {
                regions[count++] = new TextureRegion(texture, (j-1) * size, (i-1) * size, size, size);
            }
        }
        init();
    }

    public WangTile(TextureRegion[] regions) {
        this.regions = regions;
        size = regions[0].getRegionWidth();
        init();
    }

    private void init(){
        rows.addLast(new Queue<>());
        rows.last().addLast(rand(16));
        left = bot = 0;
    }

    private int rand(int range){
        return MathUtils.random(0, range - 1);
    }

    private int random(int... numbers){
        return numbers[rand(numbers.length)];
    }

    public void render(Batch batch) {
        TextureRegion[] regions = this.regions;
        int size = this.size;

        int row = 0;

        for (Queue<Integer> ids : rows) {
            int column = 0;
            for (Integer id : ids) {
                TextureRegion region = regions[id];
                batch.draw(region, left + (column * size), bot + (row * size), size, size);
                column++;
            }
            row++;
        }
    }

    public void update(OrthographicCamera cam) {
        update(Utils.camLeftX(cam), Utils.camBotY(cam), Utils.camRightX(cam), Utils.camTopY(cam));
    }
    public void update(float x, float y, float x2, float y2) {
        while (left + (size * rows.first().size) < x2){
            addRight();
        }
        while (left > x){
            addLeft();
        }
        while (bot + (size * rows.size) < y2){
            addTop();
        }
        while (bot > y){
            addBot();
        }

        while (left + (size * (rows.first().size - 1)) > x2) {
            removeRight();
        }
        while (left + size < x) {
            removeLeft();
        }
        while (bot + (size * (rows.size - 1)) > y2) {
            removeTop();
        }
        while (bot + size < y) {
            removeBot();
        }
    }

    private void removeRight() {
        for (Queue<Integer> row : rows) {
            row.removeLast();
        }
    }

    private void removeLeft() {
        for (Queue<Integer> row : rows) {
            row.removeFirst();
        }
        left += size;
    }

    private void removeTop() {
        rows.removeLast();
    }

    private void removeBot() {
        rows.removeFirst();
        bot += size;
    }

    private void addRight() {
        Integer toTheBot = null;
        for (Queue<Integer> row : rows) {
            int toTheLeft = row.last();
            Integer val;
            if (toTheBot == null){
                if (toTheLeft % 4 == 0 || (toTheLeft + 1) % 4 == 0) {
                    val = random(0, 1, 4, 5, 8, 9, 12, 13);
                } else {
                    val = random(2, 3, 6, 7, 10, 11, 14, 15);
                }
            } else {
                if (toTheLeft % 4 == 0 || (toTheLeft + 1) % 4 == 0) {
                    if (toTheBot < 8){
                        val = random(4, 5, 8, 9);
                    } else {
                        val = random(13, 14, 0, 1);
                    }
                } else {
                    if (toTheBot < 8){
                        val = random(6, 7, 10, 11);
                    } else {
                        val = random(14, 15, 2, 3);
                    }
                }
            }
            row.addLast(val);
            toTheBot = val;
        }
    }

    private void addLeft() {
        Integer toTheBot = null;
        for (Queue<Integer> row : rows) {
            int toTheRight = row.first();
            Integer val;
            if (toTheBot == null){
                if (toTheRight % 4 == 0 || (toTheRight - 1) % 4 == 0) {
                    val = random(0,4,8,12,3,7,11,15);
                } else {
                    val = random(1,2,5,6,9,10,13,14);
                }
            } else {
                if (toTheRight % 4 == 0 || (toTheRight - 1) % 4 == 0) {
                    if (toTheBot < 8){
                        val = random(4,7,8,11);
                    } else {
                        val = random(5,6,9,10);
                    }
                } else {
                    if (toTheBot < 8){
                        val = random(0,3,12,15);
                    } else {
                        val = random(1,2,13,14);
                    }
                }
            }
            row.addFirst(val);
            toTheBot = val;
        }
        left -= size;
    }

    private void addTop() {
        Integer toTheLeft = null;
        Queue<Integer> newTopQueue = new Queue<>();

        for (Integer toTheBot : rows.last()) {
            Integer val;
            if (toTheLeft == null){
                if (toTheBot < 4  || toTheBot > 11) {
                    val = random(8,9,10,11,12,13,14,15);
                } else {
                    val = random(0,1,2,3,4,5,6,7);
                }
            } else {
                if (toTheBot < 4  || toTheBot > 11) {
                    if (toTheLeft % 4 == 0 || (toTheLeft + 1) % 4 == 0){
                        val = random(8,9,12,13);
                    } else {
                        val = random(10,11,14,15);
                    }
                } else {
                    if (toTheLeft % 4 == 0 || (toTheLeft + 1) % 4 == 0){
                        val = random(0,1,4,5);
                    } else {
                        val = random(2,3,6,7);
                    }
                }
            }
            newTopQueue.addLast(val);
            toTheLeft = val;
        }
        rows.addLast(newTopQueue);
    }

    private void addBot() {
        Integer toTheLeft = null;
        Queue<Integer> newBotQueue = new Queue<>();

        for (Integer toTheTop : rows.first()) {
            Integer val;
            if (toTheLeft == null){
                if (toTheTop < 8) {
                    val = random(4, 5, 6, 7, 8, 9, 10, 11);
                } else {
                    val = random(0, 1, 2, 3, 12, 13, 14, 15);
                }
            } else {
                if (toTheTop < 8) {
                    if (toTheLeft % 4 == 0 || (toTheLeft + 1) % 4 == 0){
                        val = random(4, 5, 8, 9);
                    } else {
                        val = random(0, 1, 12, 13);
                    }
                } else {
                    if (toTheLeft % 4 == 0 || (toTheLeft + 1) % 4 == 0){
                        val = random(6, 7, 10, 11);
                    } else {
                        val = random(2, 3, 14, 15);
                    }
                }
            }
            newBotQueue.addLast(val);
            toTheLeft = val;
        }
        rows.addFirst(newBotQueue);
        bot -= size;
    }




}

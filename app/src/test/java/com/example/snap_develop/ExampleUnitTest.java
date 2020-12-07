package com.example.snap_develop;

import com.example.snap_develop.model.PostModel;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void insertPost() {

    }

    @Test
    public void connectMysql() {
        PostModel postModel = new PostModel();
        try {
            postModel.getConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

package com.bupt.liao.fred.ballpainting;

/**
 * Created by fred on 2017/7/17.
 */

public class ColorManager {
    private static ColorManager instance = null;
    final int[] colors;
    private static int COUNT = 0;
    private int currentcolor = Integer.MIN_VALUE;
    private ColorManager(){
        colors = new int[]{
//                R.color.deeppink,
//                R.color.aliceblue,
//                R.color.chartreuse,
//                R.color.tan,
                R.color.red,
                R.color.green

        };
    }

    public static ColorManager getInstance(){
        if(instance == null){
            instance = new ColorManager();
        }
        return instance;
    }

    public void getNewColor(){
        int length = colors.length;
        currentcolor = MyApplication.getAppContext().getResources().getColor(colors[COUNT % length]);
        if(COUNT < Integer.MAX_VALUE){
            COUNT++;
        }else{
            COUNT = 0;
        }
    }

    public int getCurrentColor(){
        if(currentcolor == Integer.MIN_VALUE){
            getNewColor();
        }
        return currentcolor;
    }

}

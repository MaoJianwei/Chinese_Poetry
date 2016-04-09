package org.maojianwei.chinese.poetry.database;

/**
 * Created by mao on 4/9/16.
 */
public class MaoPoetryItem {

    String title;
    String dynasty;
    String poet;
    String poem;


    public void setTitle(String title){
        this.title = title;
    }
    public void setDynasty(String dynasty){
        this.dynasty = dynasty;
    }
    public void setPoet(String poet){
        this.poet = poet;
    }
    public void setPoem(String poem){
        this.poem = poem;
    }
    public String getTitle(){
        return title;
    }
    public String getDynasty(){
        return dynasty;
    }
    public String getPoet(){
        return poet;
    }
    public String getPoem(){
        return poem;
    }





}

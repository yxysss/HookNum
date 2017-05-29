package com.example.yxy.hooknum;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.*;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.sharesdk.onekeyshare.OnekeyShare;

import java.lang.reflect.Field;
import java.util.Random;

public class hooknum extends AppCompatActivity implements View.OnClickListener, View.OnTouchListener{

    private boolean touchable = true;

    private int DisplayWidth, DisplayHeight;
    private int chessmargin, smallchessmargin;
    private int chessWidth, smallchessWidth;
    private int itemWidth;
    private void measurechessboard() {
        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        DisplayWidth = metric.widthPixels;     // 屏幕宽度（像素）
        DisplayHeight = metric.heightPixels;   // 屏幕高度（像素）
        System.out.println(DisplayWidth);
        chessmargin = DisplayWidth * 8 / 1000;
        chessWidth = DisplayWidth * 24 / 100;
        smallchessWidth = DisplayWidth * 24 * 99 / 10000;
        smallchessmargin = (DisplayWidth - 4 * smallchessWidth) / 5;
        itemWidth = DisplayWidth * 12 / 100;
    }

    private int GAME_STATE = 0;
    private RelativeLayout maskboard, rlcurrentmasks, rlhighestmasks, rlhighesttitle;
    private TextView textcurrentmasks, highestmasks, highesttitle;
    private ImageView pending1, pending2;
    private int pending1value, pending2value;
    private RelativeLayout.LayoutParams maskboardlp, pending1lp, pending2lp, progressbarlp, rlcurrentmaskslp, rlhighestmaskslp, rlhighesttitlelp;
    private MyProgressBar progressBar;
    private void loadmaskboardandprogressbar() {
        maskboard = (RelativeLayout) findViewById(R.id.maskboard);
        maskboardlp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, smallchessWidth);
        maskboardlp.addRule(RelativeLayout.ABOVE, R.id.progressbar);
        maskboard.setLayoutParams(maskboardlp);
        progressBar = (MyProgressBar) findViewById(R.id.progressbar);
        progressbarlp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, smallchessWidth/2);
        progressbarlp.addRule(RelativeLayout.ABOVE, R.id.chessboard);
        progressBar.setLayoutParams(progressbarlp);
        rlcurrentmasks = (RelativeLayout) findViewById(R.id.currentmasks);
        rlcurrentmaskslp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        rlcurrentmaskslp.addRule(RelativeLayout.ALIGN_PARENT_START);
        rlcurrentmasks.setLayoutParams(rlcurrentmaskslp);
        rlhighestmasks =  (RelativeLayout) findViewById(R.id.everhighestmasks);
        rlhighestmaskslp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, smallchessWidth/2);
        rlhighestmaskslp.addRule(RelativeLayout.RIGHT_OF, R.id.currentmasks);
        rlhighestmaskslp.addRule(RelativeLayout.BELOW, R.id.everhighesttitle);
        rlhighestmaskslp.setMargins(smallchessmargin, 0, 0, 0);
        rlhighestmasks.setLayoutParams(rlhighestmaskslp);
        rlhighesttitle = (RelativeLayout) findViewById(R.id.everhighesttitle);
        rlhighesttitlelp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, smallchessWidth/2);
        rlhighesttitlelp.addRule(RelativeLayout.RIGHT_OF, R.id.currentmasks);
        rlhighesttitlelp.setMargins(smallchessmargin, 0, 0, 0);
        rlhighesttitle.setLayoutParams(rlhighesttitlelp);
        textcurrentmasks = (TextView) findViewById(R.id.textcurrentmasks);
        textcurrentmasks.setText("0");
        textcurrentmasks.setTextSize(smallchessWidth/4);
        textcurrentmasks.setTextColor(R.color.colorAccent);
        highestmasks = (TextView) findViewById(R.id.highestmasks);
        highestmasks.setText("0");
        highestmasks.setTextSize(smallchessWidth/8);
        highesttitle = (TextView) findViewById(R.id.highesttitle);
        highesttitle.setText("0");
        highesttitle.setTextSize(smallchessWidth/8);
        pending1 = (ImageView) findViewById(R.id.pending1);
        pending2 = (ImageView) findViewById(R.id.pending2);
        pending1.setImageResource(R.color.colorAccent);
        pending2.setImageResource(R.color.colorAccent);
        pending1lp = new RelativeLayout.LayoutParams(smallchessWidth*8/10, smallchessWidth*8/10);
        pending1lp.setMargins(0, 0, smallchessmargin, smallchessmargin);
        pending1lp.addRule(RelativeLayout.ALIGN_PARENT_END);
        pending1lp.addRule(RelativeLayout.ABOVE, R.id.progressbar);
        pending1.setLayoutParams(pending1lp);
        pending1value = 1;
        pending1.setImageResource(getImageId(pending1value));
        pending1.setZ(20);
        pending2lp = new RelativeLayout.LayoutParams(smallchessWidth/2, smallchessWidth/2);
        pending2lp.setMargins(0, 0, smallchessmargin, smallchessmargin);
        pending2lp.addRule(RelativeLayout.LEFT_OF, R.id.pending1);
        pending2lp.addRule(RelativeLayout.ABOVE, R.id.progressbar);
        pending2.setLayoutParams(pending2lp);
        pending2value = 1;
        pending2.setImageResource(getImageId(pending2value));

        Log.d("progressBar", "progressBar");
    }

    private int chess[][] = new int[10][10];
    private int hassavedbefore = 0;
    private int savedchess[][] = new int[10][10];
    private int checkhassaved() {
        return 1;
    }
    private void initchessboard() {
        for(int i = 0; i <= 5; i ++) {
            for(int j = 0; j <= 5; j ++) {
                chess[i][j] = -1;
            }
        }
        if(checkhassaved() == hassavedbefore) {
            for(int i = 1; i <= 4; i ++) {
                for(int j = 1; j <= 4; j ++) {
                    chess[i][j] = savedchess[i][j];
                }
            }
        } else {
            for(int i = 1; i <= 4; i ++) {
                for(int j = 1; j <= 4; j ++) {
                    chess[i][j] = 0;
                }
            }
        }
    }

    private RelativeLayout chessboard;
    private RelativeLayout.LayoutParams chessboardlp;
    private ImageView chessboardbackground;
    private ImageView imageViewchess[][] = new ImageView[10][10];
    private ImageView smallimageViewchess[][] = new ImageView[10][10];
    private RelativeLayout.LayoutParams chesslp[][] = new RelativeLayout.LayoutParams[10][10];
    private RelativeLayout.LayoutParams smallchesslp[][] = new RelativeLayout.LayoutParams[10][10];
    private void loadchessboardview() {
        chessboard = (RelativeLayout) findViewById(R.id.chessboard);
        chessboardlp = new RelativeLayout.LayoutParams(DisplayWidth, DisplayWidth);
        chessboardlp.addRule(RelativeLayout.ABOVE, R.id.itemsboard);
        chessboard.setLayoutParams(chessboardlp);
        chessboardbackground = (ImageView) findViewById(R.id.chessboardbackground);
        //chessboardbackground.setLayoutParams(chessboardlp);
        chessboardbackground.setImageResource(R.color.chessboardbackground);
        for(int i = 1; i <= 4; i ++) {
            for(int j = 1; j <= 4; j ++) {
                imageViewchess[i][j] = (ImageView)findViewById(getchesslocationId(i, j));
                imageViewchess[i][j].setImageResource(getImageId(chess[i][j]));
                smallimageViewchess[i][j] = (ImageView)findViewById(getsmallchesslocationId(i, j));
                smallimageViewchess[i][j].setImageResource(R.color.chessbackground);
                chesslp[i][j] = new RelativeLayout.LayoutParams(chessWidth, chessWidth);
                smallchesslp[i][j] = new RelativeLayout.LayoutParams(smallchessWidth, smallchessWidth);
                chesslp[i][j].setMargins(chessmargin, chessmargin, 0, 0);
                smallchesslp[i][j].setMargins(smallchessmargin, smallchessmargin, 0, 0);
            }
        }
        chesslp[1][2].addRule(RelativeLayout.RIGHT_OF, R.id.chess1);
        chesslp[1][3].addRule(RelativeLayout.RIGHT_OF, R.id.chess2);
        chesslp[1][4].addRule(RelativeLayout.RIGHT_OF, R.id.chess3);
        chesslp[2][1].addRule(RelativeLayout.BELOW, R.id.chess1);
        chesslp[2][2].addRule(RelativeLayout.RIGHT_OF, R.id.chess5);
        chesslp[2][2].addRule(RelativeLayout.BELOW, R.id.chess2);
        chesslp[2][3].addRule(RelativeLayout.RIGHT_OF, R.id.chess6);
        chesslp[2][3].addRule(RelativeLayout.BELOW, R.id.chess3);
        chesslp[2][4].addRule(RelativeLayout.RIGHT_OF, R.id.chess7);
        chesslp[2][4].addRule(RelativeLayout.BELOW, R.id.chess4);
        chesslp[3][1].addRule(RelativeLayout.BELOW, R.id.chess5);
        chesslp[3][2].addRule(RelativeLayout.RIGHT_OF, R.id.chess9);
        chesslp[3][2].addRule(RelativeLayout.BELOW, R.id.chess6);
        chesslp[3][3].addRule(RelativeLayout.RIGHT_OF, R.id.chess10);
        chesslp[3][3].addRule(RelativeLayout.BELOW, R.id.chess7);
        chesslp[3][4].addRule(RelativeLayout.RIGHT_OF, R.id.chess11);
        chesslp[3][4].addRule(RelativeLayout.BELOW, R.id.chess8);
        chesslp[4][1].addRule(RelativeLayout.BELOW, R.id.chess9);
        chesslp[4][2].addRule(RelativeLayout.RIGHT_OF, R.id.chess13);
        chesslp[4][2].addRule(RelativeLayout.BELOW, R.id.chess10);
        chesslp[4][3].addRule(RelativeLayout.RIGHT_OF, R.id.chess14);
        chesslp[4][3].addRule(RelativeLayout.BELOW, R.id.chess11);
        chesslp[4][4].addRule(RelativeLayout.RIGHT_OF, R.id.chess15);
        chesslp[4][4].addRule(RelativeLayout.BELOW, R.id.chess12);
        smallchesslp[1][2].addRule(RelativeLayout.RIGHT_OF, R.id.smallchess1);
        smallchesslp[1][3].addRule(RelativeLayout.RIGHT_OF, R.id.smallchess2);
        smallchesslp[1][4].addRule(RelativeLayout.RIGHT_OF, R.id.smallchess3);
        smallchesslp[2][1].addRule(RelativeLayout.BELOW, R.id.smallchess1);
        smallchesslp[2][2].addRule(RelativeLayout.RIGHT_OF, R.id.smallchess5);
        smallchesslp[2][2].addRule(RelativeLayout.BELOW, R.id.smallchess2);
        smallchesslp[2][3].addRule(RelativeLayout.RIGHT_OF, R.id.smallchess6);
        smallchesslp[2][3].addRule(RelativeLayout.BELOW, R.id.smallchess3);
        smallchesslp[2][4].addRule(RelativeLayout.RIGHT_OF, R.id.smallchess7);
        smallchesslp[2][4].addRule(RelativeLayout.BELOW, R.id.smallchess4);
        smallchesslp[3][1].addRule(RelativeLayout.BELOW, R.id.smallchess5);
        smallchesslp[3][2].addRule(RelativeLayout.RIGHT_OF, R.id.smallchess9);
        smallchesslp[3][2].addRule(RelativeLayout.BELOW, R.id.smallchess6);
        smallchesslp[3][3].addRule(RelativeLayout.RIGHT_OF, R.id.smallchess10);
        smallchesslp[3][3].addRule(RelativeLayout.BELOW, R.id.smallchess7);
        smallchesslp[3][4].addRule(RelativeLayout.RIGHT_OF, R.id.smallchess11);
        smallchesslp[3][4].addRule(RelativeLayout.BELOW, R.id.smallchess8);
        smallchesslp[4][1].addRule(RelativeLayout.BELOW, R.id.smallchess9);
        smallchesslp[4][2].addRule(RelativeLayout.RIGHT_OF, R.id.smallchess13);
        smallchesslp[4][2].addRule(RelativeLayout.BELOW, R.id.smallchess10);
        smallchesslp[4][3].addRule(RelativeLayout.RIGHT_OF, R.id.smallchess14);
        smallchesslp[4][3].addRule(RelativeLayout.BELOW, R.id.smallchess11);
        smallchesslp[4][4].addRule(RelativeLayout.RIGHT_OF, R.id.smallchess15);
        smallchesslp[4][4].addRule(RelativeLayout.BELOW, R.id.smallchess12);
        for(int i = 1; i <= 4; i ++) {
            for(int j = 1; j <= 4; j ++) {
                imageViewchess[i][j].setLayoutParams(chesslp[i][j]);
                smallimageViewchess[i][j].setLayoutParams(smallchesslp[i][j]);
                imageViewchess[i][j].setOnTouchListener(this);
            }
        }
    }

    private void showShare() {
        OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
        oks.disableSSOWhenAuthorize();
        // title标题，印象笔记、邮箱、信息、微信、人人网、QQ和QQ空间使用
        oks.setTitle("标题");
        // titleUrl是标题的网络链接，仅在Linked-in,QQ和QQ空间使用
        oks.setTitleUrl("http://sharesdk.cn");
        // text是分享文本，所有平台都需要这个字段
        oks.setText("我是分享文本");
        //分享网络图片，新浪微博分享网络图片需要通过审核后申请高级写入接口，否则请注释掉测试新浪微博
        oks.setImageUrl("http://f1.sharesdk.cn/imgs/2014/02/26/owWpLZo_638x960.jpg");
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
        //oks.setImagePath("/sdcard/test.jpg");//确保SDcard下面存在此张图片
        // url仅在微信（包括好友和朋友圈）中使用
        oks.setUrl("http://sharesdk.cn");
        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
        oks.setComment("我是测试评论文本");
        // site是分享此内容的网站名称，仅在QQ空间使用
        oks.setSite("ShareSDK");
        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
        oks.setSiteUrl("http://sharesdk.cn");

// 启动分享GUI
        oks.show(this);
    }

    private ImageView menuitem, advitem, bombitem;
    private ImageView imrestart, imcontinue, immusic, imshare;
    private RelativeLayout rlrestart, rlcontinue, rlmusic, rlshare;
    private View menuview;
    private TextView levelnum;
    private RelativeLayout.LayoutParams menulp, advlp, bomblp;
    private RelativeLayout.LayoutParams rlrestartlp, rlcontinuelp, rlmusiclp, rlsharelp;
    private RelativeLayout.LayoutParams imrestartlp, imcontinuelp, immusiclp, imsharelp;
    private AlertDialog menudialog;
    private Snackbar advsnackbar;
    private int bombnum;
    private void loaditemview() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        menudialog = builder.create();
        menuview = View.inflate(this, R.layout.dialog_menu, null);
        rlrestart = (RelativeLayout) menuview.findViewById(R.id.rlrestart);
        rlrestartlp = new RelativeLayout.LayoutParams(DisplayWidth/3, DisplayWidth/3);
        rlrestart.setLayoutParams(rlrestartlp);
        imrestart = (ImageView) menuview.findViewById(R.id.imrestart);
        imrestartlp = new RelativeLayout.LayoutParams(DisplayWidth*4/15, DisplayWidth*4/15);
        imrestartlp.addRule(RelativeLayout.CENTER_IN_PARENT);
        imrestart.setLayoutParams(imrestartlp);
        imrestart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(hooknum.this, hooknum.class);
                startActivity(intent);
            }
        });
        rlcontinue = (RelativeLayout) menuview.findViewById(R.id.rlcontinue);
        rlcontinuelp = new RelativeLayout.LayoutParams(DisplayWidth/3, DisplayWidth/3);
        rlcontinuelp.addRule(RelativeLayout.RIGHT_OF, R.id.rlrestart);
        rlcontinue.setLayoutParams(rlcontinuelp);
        imcontinue = (ImageView) menuview.findViewById(R.id.imcontinue);
        imcontinue.setLayoutParams(imrestartlp);
        imcontinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (menudialog != null) {
                    menudialog.dismiss();
                }
            }
        });
        rlmusic = (RelativeLayout) menuview.findViewById(R.id.rlmusic);
        rlmusiclp = new RelativeLayout.LayoutParams(DisplayWidth/3, DisplayWidth/3);
        rlmusiclp.addRule(RelativeLayout.BELOW, R.id.rlrestart);
        rlmusic.setLayoutParams(rlmusiclp);
        immusic = (ImageView) menuview.findViewById(R.id.immusic);
        immusic.setLayoutParams(imrestartlp);
        immusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        rlshare = (RelativeLayout) menuview.findViewById(R.id.rlshare);
        rlsharelp = new RelativeLayout.LayoutParams(DisplayWidth/3,DisplayWidth/3);
        rlsharelp.addRule(RelativeLayout.BELOW, R.id.rlrestart);
        rlsharelp.addRule(RelativeLayout.RIGHT_OF, R.id.rlrestart);
        rlshare.setLayoutParams(rlsharelp);
        imshare = (ImageView) menuview.findViewById(R.id.imshare);
        imshare.setLayoutParams(imrestartlp);
        imshare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showShare();
            }
        });
        menudialog.setView(menuview);
        menuitem = (ImageView) findViewById(R.id.menuitem);
        menuitem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menudialog.show();
                menudialog.getWindow().setLayout(DisplayWidth*3/4, DisplayWidth*3/4);
            }
        });
        advitem = (ImageView) findViewById(R.id.advitem);
        advitem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                advsnackbar.show();
            }
        });
        advsnackbar = Snackbar.make(advitem, "观看广告即可获得一次炸弹机会，一局只能使用一次哦~", Snackbar.LENGTH_SHORT)
                .setAction("观看", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
        bombitem = (ImageView) findViewById(R.id.bombitem);
        menuitem.setImageResource(R.color.colorPrimary);
        advitem.setImageResource(R.color.colorPrimary);
        bombitem.setImageResource(R.color.colorPrimary);
        menulp = new RelativeLayout.LayoutParams(itemWidth, itemWidth);
        menulp.setMargins(itemWidth/15, itemWidth/15, 0, 0);
        menuitem.setLayoutParams(menulp);
        advlp = new RelativeLayout.LayoutParams(itemWidth, itemWidth);
        advlp.setMargins(itemWidth/15, itemWidth/15, 0 , 0);
        advlp.addRule(RelativeLayout.RIGHT_OF, R.id.menuitem);
        advitem.setLayoutParams(advlp);
        bomblp = new RelativeLayout.LayoutParams(itemWidth, itemWidth);
        bomblp.setMargins(itemWidth/15, itemWidth/15, 0, 0);
        bomblp.addRule(RelativeLayout.RIGHT_OF, R.id.advitem);
        bombitem.setLayoutParams(bomblp);
        bombitem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GAME_STATE = 1;
            }
        });
        levelnum = (TextView) findViewById(R.id.levelnum);
        levelnum.setTextSize(smallchessWidth/6);
        levelnum.setText("Level 1");

    }

    private boolean animationload = false;
    private AnimationSet[] animationSets = new AnimationSet[21];
    private ScaleAnimation[] scaleAnimations = new ScaleAnimation[21];
    private TranslateAnimation move[] = new TranslateAnimation[21];
    private TranslateAnimation moveanimation[] = new TranslateAnimation[11];
    private int movetime[] = new int[11];
    private void loadnewmoveanimation() {
        int centerlocation[] = new int[2];
        pending1.getLocationOnScreen(centerlocation);
        centerlocation[0] -= chessWidth / 2;
        centerlocation[1] -= chessWidth / 2;

        int targetlocation[] = new int[2];
        float distanceinx, distanceiny;

        imageViewchess[1][1].getLocationOnScreen(targetlocation);
        targetlocation[0] -= smallchessWidth * 4/10;
        targetlocation[1] -= smallchessWidth * 4/10;
        distanceinx = targetlocation[0] - centerlocation[0];
        distanceiny = targetlocation[1] - centerlocation[1];
        double distance0 = distanceinx * distanceinx + distanceiny * distanceiny;
        distance0 = Math.sqrt(distance0);

        for (int i = 1; i <= 4; i ++) {
            for (int j = 1; j <= 4; j++) {
                imageViewchess[i][j].getLocationOnScreen(targetlocation);
                targetlocation[0] -= smallchessWidth * 4/10;
                targetlocation[1] -= smallchessWidth * 4/10;
                distanceinx = targetlocation[0] - centerlocation[0];
                distanceiny = targetlocation[1] - centerlocation[1];
                double distance = distanceinx * distanceinx + distanceiny * distanceiny;
                distance = Math.sqrt(distance);
                move[i*4-4+j] = new TranslateAnimation(0, distanceinx/1.25f, 0, distanceiny/1.25f);
                //move[i*4-4+j].setDuration((int)(distance*1000/distance0));
                move[i*4-4+j].setDuration(1000);
                scaleAnimations[i*4-4+j] = new ScaleAnimation(1, 1.25f, 1, 1.25f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                scaleAnimations[i*4-4+j].setDuration(1000);
                animationSets[i*4-4+j] = new AnimationSet(true);
                animationSets[i*4-4+j].addAnimation(move[i*4-4+j]);
                animationSets[i*4-4+j].addAnimation(scaleAnimations[i*4-4+j]);
                animationSets[i*4-4+j].setDuration(1000);
            }
        }
    }
    private void loadmoveanimation() {

        movetime[1] = 100;
        movetime[2] = 90;
        movetime[3] = 80;
        movetime[4] = 70;
        movetime[5] = 60;
        movetime[6] = 50;
        movetime[7] = 40;
        movetime[8] = 30;

        int centerlocation[] = new int[2];
        imageViewchess[2][2].getLocationOnScreen(centerlocation);
        int targetlocation[] = new int[2];

        int distanceinx, distanceiny;

        imageViewchess[1][1].getLocationOnScreen(targetlocation);
        distanceinx = targetlocation[0] - centerlocation[0];
        distanceiny = targetlocation[1] - centerlocation[1];
        moveanimation[8] = new TranslateAnimation(0, distanceinx, 0, distanceiny);

        imageViewchess[1][2].getLocationOnScreen(targetlocation);
        distanceinx = targetlocation[0] - centerlocation[0];
        distanceiny = targetlocation[1] - centerlocation[1];
        moveanimation[7] = new TranslateAnimation(0, distanceinx, 0, distanceiny);

        imageViewchess[1][3].getLocationOnScreen(targetlocation);
        distanceinx = targetlocation[0] - centerlocation[0];
        distanceiny = targetlocation[1] - centerlocation[1];
        moveanimation[6] = new TranslateAnimation(0, distanceinx, 0, distanceiny);

        imageViewchess[2][1].getLocationOnScreen(targetlocation);
        distanceinx = targetlocation[0] - centerlocation[0];
        distanceiny = targetlocation[1] - centerlocation[1];
        moveanimation[5] = new TranslateAnimation(0, distanceinx, 0, distanceiny);

        imageViewchess[2][3].getLocationOnScreen(targetlocation);
        distanceinx = targetlocation[0] - centerlocation[0];
        distanceiny = targetlocation[1] - centerlocation[1];
        moveanimation[4] = new TranslateAnimation(0, distanceinx, 0, distanceiny);

        imageViewchess[3][1].getLocationOnScreen(targetlocation);
        distanceinx = targetlocation[0] - centerlocation[0];
        distanceiny = targetlocation[1] - centerlocation[1];
        moveanimation[3] = new TranslateAnimation(0, distanceinx, 0, distanceiny);

        imageViewchess[3][2].getLocationOnScreen(targetlocation);
        distanceinx = targetlocation[0] - centerlocation[0];
        distanceiny = targetlocation[1] - centerlocation[1];
        moveanimation[2] = new TranslateAnimation(0, distanceinx, 0, distanceiny);

        imageViewchess[3][3].getLocationOnScreen(targetlocation);
        distanceinx = targetlocation[0] - centerlocation[0];
        distanceiny = targetlocation[1] - centerlocation[1];
        moveanimation[1] = new TranslateAnimation(0, distanceinx, 0, distanceiny);

    }

    // RecyclerView.Adapter


    private int getStatusBarHeight() {
        Class<?> c = null;
        Object obj = null;
        Field field = null;
        int x = 0, sbar = 0;

        try {
            c = Class.forName("com.android.internal.R$dimen");
            obj = c.newInstance();
            field = c.getField("status_bar_height");
            x = Integer.parseInt(field.get(obj).toString());
            sbar = getResources().getDimensionPixelSize(x);
        } catch (Exception e1) {
            e1.printStackTrace();
        }

        return sbar;
    }
    private int statusbarheight;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hooknum);
        statusbarheight = getStatusBarHeight();
        ActivityCollector.add(this);
        measurechessboard();
        loadmaskboardandprogressbar();
        initchessboard();
        loadchessboardview();
        loaditemview();
    }

    private int getImageId(int value) {
        switch (value) {
            case 0:
                return R.color.transparent;
            case 1:
                return R.mipmap.s1;
            case 2:
                return R.mipmap.s2;
            case 3:
                return R.mipmap.s3;
            case 4:
                return R.mipmap.s4;
            case 5:
                return R.mipmap.s5;
            case 6:
                return R.mipmap.s6;
            case 7:
                return R.mipmap.s7;
            case 8:
                return R.mipmap.s8;
            case 9:
                return R.mipmap.s9;
            case 10:
                return R.mipmap.s10;
            case 11:
                return R.mipmap.s11;
            case 12:
                return R.mipmap.s12;
            case 13:
                return R.mipmap.s13;
            case 14:
                return R.mipmap.s14;
            case 15:
                return R.mipmap.s15;
            case 16:
                return R.mipmap.s16;
            case 17:
                return R.mipmap.s17;
            case 18:
                return R.mipmap.s18;
            case 19:
                return R.mipmap.s19;
            default:
                return R.color.transparent;
        }
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (touchable == false) return false;
        System.out.println("onTouch");
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                System.out.println("ACTION_DOWN");
                touchhandler(view);
                break;
            default:
                break;
        }
        return false;
    }
    private Bundle getviewindex(View view) {
        Bundle bundle = new Bundle();
        switch (view.getId()) {
            case R.id.chess1:
                bundle.putInt("x", 1); bundle.putInt("y", 1); break;
            case R.id.chess2:
                bundle.putInt("x", 1); bundle.putInt("y", 2); break;
            case R.id.chess3:
                bundle.putInt("x", 1); bundle.putInt("y", 3); break;
            case R.id.chess4:
                bundle.putInt("x", 1); bundle.putInt("y", 4); break;
            case R.id.chess5:
                bundle.putInt("x", 2); bundle.putInt("y", 1); break;
            case R.id.chess6:
                bundle.putInt("x", 2); bundle.putInt("y", 2); break;
            case R.id.chess7:
                bundle.putInt("x", 2); bundle.putInt("y", 3); break;
            case R.id.chess8:
                bundle.putInt("x", 2); bundle.putInt("y", 4); break;
            case R.id.chess9:
                bundle.putInt("x", 3); bundle.putInt("y", 1); break;
            case R.id.chess10:
                bundle.putInt("x", 3); bundle.putInt("y", 2); break;
            case R.id.chess11:
                bundle.putInt("x", 3); bundle.putInt("y", 3); break;
            case R.id.chess12:
                bundle.putInt("x", 3); bundle.putInt("y", 4); break;
            case R.id.chess13:
                bundle.putInt("x", 4); bundle.putInt("y", 1); break;
            case R.id.chess14:
                bundle.putInt("x", 4); bundle.putInt("y", 2); break;
            case R.id.chess15:
                bundle.putInt("x", 4); bundle.putInt("y", 3); break;
            case R.id.chess16:
                bundle.putInt("x", 4); bundle.putInt("y", 4); break;
            default:
                bundle.putInt("x", 0); bundle.putInt("y", 0); break;
        }
        return bundle;
    }
    private void touchhandler(View view) {

        if (animationload == false) {
            loadnewmoveanimation();
            loadmoveanimation();
            fireworkprepare();
            animationload = true;
            Log.d("getZ", "chessboard.Z : " + chessboard.getZ());
        }

        touchable = false;
        Bundle bundle = getviewindex(view);
        x = bundle.getInt("x");
        y = bundle.getInt("y");
        if(x>0&x<5&y>0&y<5) {

            if (GAME_STATE == 1) {
                if (chess[x][y] == 0) {
                    GAME_STATE = 0;
                    touchable = true;
                    return ;
                }
                chess[x][y] = 0;
                imageViewchess[x][y].setImageResource(getImageId(0));

                GAME_STATE = 0;
                touchable = true;
                return ;

            }
            if (chess[x][y] != 0) {
                touchable = true;
                return ;
            }
            animationSets[x*4-4+y].setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    chess[x][y] = pending1value;
                    imageViewchess[x][y].setImageResource(getImageId(chess[x][y]));
                    eliminate();
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            pending1.startAnimation(animationSets[x*4-4+y]);
            nextchange();
            /*
            if(GAME_STATE == 0)  {
                this.x = x;
                this.y = y;
                System.out.println(x + " " + y);
                //eliminate();
                GAME_STATE = -1;
                return ;
            }
            */
        }
    }



    private int getchesslocationId(int x, int y) {

        switch (x*4 - 4 + y) {
            case 1:
                return R.id.chess1;
            case 2:
                return R.id.chess2;
            case 3:
                return R.id.chess3;
            case 4:
                return R.id.chess4;
            case 5:
                return R.id.chess5;
            case 6:
                return R.id.chess6;
            case 7:
                return R.id.chess7;
            case 8:
                return R.id.chess8;
            case 9:
                return R.id.chess9;
            case 10:
                return R.id.chess10;
            case 11:
                return R.id.chess11;
            case 12:
                return R.id.chess12;
            case 13:
                return R.id.chess13;
            case 14:
                return R.id.chess14;
            case 15:
                return R.id.chess15;
            case 16:
                return R.id.chess16;
            default:
                return 0;
        }
    }
    private int getsmallchesslocationId(int x, int y) {

        switch (x*4 - 4 + y) {
            case 1:
                return R.id.smallchess1;
            case 2:
                return R.id.smallchess2;
            case 3:
                return R.id.smallchess3;
            case 4:
                return R.id.smallchess4;
            case 5:
                return R.id.smallchess5;
            case 6:
                return R.id.smallchess6;
            case 7:
                return R.id.smallchess7;
            case 8:
                return R.id.smallchess8;
            case 9:
                return R.id.smallchess9;
            case 10:
                return R.id.smallchess10;
            case 11:
                return R.id.smallchess11;
            case 12:
                return R.id.smallchess12;
            case 13:
                return R.id.smallchess13;
            case 14:
                return R.id.smallchess14;
            case 15:
                return R.id.smallchess15;
            case 16:
                return R.id.smallchess16;
            default:
                return 0;
        }
    }

    private int currentmasks = 0;
    private int round = 0;
    private int roundnumber = 0;
    private int eliminatequeue[][] = new int[10][10];
    private int scoreinround[] = new int[10];
    private void initeliminatequeue() {
        for (int i = 1; i <= 9; i ++) {
            eliminatequeue[i][0] = 0;
            scoreinround[i] = 0;
        }
    }

    private int chessmax;

    private RelativeLayout mainview;

    private RelativeLayout failurebanner;

    private RelativeLayout.LayoutParams bannerlp;

    private void performfailure() {
        mainview = (RelativeLayout) findViewById(R.id.activity_hooknum);
        failurebanner = (RelativeLayout)View.inflate(this, R.layout.banner_failure, null);
        bannerlp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        failurebanner.setLayoutParams(bannerlp);
        mainview.addView(failurebanner);

    }

    private void checkfailure() {
        int mark = 0;
        for (int i = 1; i <= 4; i ++) {
            for (int j = 1; j <= 4; j ++) {
                if (chess[i][j] == 0) {
                    mark = 1;
                    break;
                }
            }
        }
        if (mark == 0) {
            performfailure();
        }
    }

    private int x, y;
    private int[] time = {0,54,54,54,54,54,54,54,54,54};
    private int[] masks = {0, 100, 300, 600, 1000, 1500, 2100, 2800, 3600, 4500, 5500};
    private int level = 1;
    private int dx[] = {0, -1, -1, -1, 0, 0, 1, 1, 1};
    private int dy[] = {0, -1, 0, 1, -1, 1, -1, 0, 1};
    private int side;
    private int sidex;
    private int sidey;
    private void nextchange() {
        int[] location1 = new int[2];
        int[] location2 = new int[2];
        pending1.getLocationOnScreen(location1);
        pending2.getLocationOnScreen(location2);

        TranslateAnimation nextanimation = new TranslateAnimation(0, location2[0]-location1[0], 0, location2[1]-location1[1]);
        ScaleAnimation scaleAnimation = new ScaleAnimation(1f, 2f, 1f, 2f);
        AnimationSet animationSet = new AnimationSet(true);
        animationSet.addAnimation(nextanimation);
        animationSet.addAnimation(scaleAnimation);
        animationSet.setDuration(1000);
        pending2.startAnimation(animationSet);
    }
    private void execute() {
        final ScaleAnimation freeze = new ScaleAnimation(1, 1, 1, 1);
        freeze.setDuration(40);
        freeze.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                execute();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        if (round >= roundnumber) {
            if (roundnumber != 0) shotfirework(1000);
            touchable = true;
            return ;
        }
        round ++;
        for(int i = 1; i <= eliminatequeue[round][0]; i ++) {
            side = eliminatequeue[round][i];
            move[side] = moveanimation[side];
            move[side].setDuration(time[round]);
            if(i == 1) {
                move[side].setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        // center chess largen & firework effect
                                    /*
                                    chess[x][y] = chess[x][y] + 1;
                                    rmipmapid = getrmipmapid(chess[x][y]);
                                    smallimageViewchess[x][y].setImageResource(rmipmapid);
                                    smallimageViewchess[x][y].startAnimation(largen[round]);
                                    Message message = new Message();
                                    message.what = smallimageviewchesslargen;
                                    handler.sendMessage(message);
                                    if(chess[x][y] > chessmax) {
                                        Message message1 = new Message();
                                        message1.what = fireworkshotbig;
                                        handler.sendMessage(message1);
                                        chessmax = chess[x][y];
                                    } else {
                                        Message message2 = new Message();
                                        message2.what = fireworkshotsmall;
                                        handler.sendMessage(message2);
                                    }
                                    */
                        chess[x][y] += 1;
                        imageViewchess[x][y].setImageResource(getImageId(chess[x][y]));
                        for (int j = 1; j <= eliminatequeue[round][0]; j ++) {
                            chess[x+dx[eliminatequeue[round][j]]][y+dy[eliminatequeue[round][j]]] = 0;
                            imageViewchess[x+dx[eliminatequeue[round][j]]][y+dy[eliminatequeue[round][j]]].setImageResource(R.color.transparent);
                        }
                        imageViewchess[x][y].startAnimation(freeze);
                        // execute();
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                    }
                });
            } else {
                move[side].setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                    }
                });
            }
        }

        imageViewchess[x][y].setZ(5);
        for (int j = 1; j <= eliminatequeue[round][0]; j ++) {
            // chess[x + dx[eliminatequeue[round][j]]][y + dy[eliminatequeue[round][j]]] = 0;
            imageViewchess[x + dx[eliminatequeue[round][j]]][y + dy[eliminatequeue[round][j]]].setZ(10);
        }

        for(int i = 1; i <= eliminatequeue[round][0]; i ++) {
            side = eliminatequeue[round][i];
            sidex = x + dx[eliminatequeue[round][i]];
            sidey = y + dy[eliminatequeue[round][i]];

            imageViewchess[sidex][sidey].startAnimation(move[side]);
        }
    }
    private void eliminate() {
        int chessvalue = chess[x][y];
        round = 1;
        initeliminatequeue();
        System.out.println("chessvalue: " + chessvalue);
        while(true) {
            for (int i = 1; i <= 8; i++) {
                if (chess[x + dx[i]][y + dy[i]] == chessvalue) {
                    System.out.println("round: " + round + ", " + "len: " + eliminatequeue[round][0]);
                    eliminatequeue[round][++eliminatequeue[round][0]] = i;
                    scoreinround[round] += chessvalue;
                }
            }
            if(eliminatequeue[round][0] == 0) break;
            chessvalue ++;
            currentmasks += scoreinround[round];
            round ++;
        }
        roundnumber = round - 1;
        round = 0;
        System.out.println("roundnumber : " + roundnumber + " round : " + round );

        if (roundnumber != 0) execute();
        else {
            checkfailure();
        }

        Log.d("ProgressBar", currentmasks + "");
        if (roundnumber > 0) {
            progressBar.setProgress((float)(currentmasks-masks[level-1])/(float)(masks[level]-masks[level-1]), roundnumber * 94 - 40);
            textcurrentmasks.setText(currentmasks+"");
            if (currentmasks > masks[level]) level ++;
            levelnum.setText("Level " + level);
        }
    }


    private int[][] chessloaction = new int[17][3];
    private ImageView[] firework = new ImageView[22];
    private RelativeLayout.LayoutParams fireworklp;
    private void fireworkprepare() {

        for(int i = 1; i <= 4; i ++) {
            for(int j = 1; j <= 4; j ++) {
                imageViewchess[i][j].getLocationInWindow(chessloaction[(i-1)*4+j]);
            }
        }

        fireworklp = new RelativeLayout.LayoutParams(smallchessWidth/8, smallchessWidth/8);

        for(int i = 1; i <= 20;i ++) {
            firework[i] = new ImageView(hooknum.this);
            chessboard.addView(firework[i]);
            firework[i].setLayoutParams(fireworklp);
            firework[i].setImageResource(R.color.transparent);
            firework[i].setZ(20);
        }

    }

    private TranslateAnimation[] fireworkanimation = new TranslateAnimation[22];
    private void shotfirework(int time) {

        for(int i = 1; i <= 20; i ++) {
            Log.d("shotfirework", x + " : " + y);
            Log.d("setX", chessloaction[(x-1)*4+y][0] + "");
            Log.d("setY", chessloaction[(x-1)*4+y][1] + "");
            firework[i].setX(chessloaction[(x-1)*4+y][0]+chessWidth/2-smallchessWidth/16);
            firework[i].setY(chessloaction[(x-1)*4+y][1]-chessboard.getY()-statusbarheight+chessWidth/2-smallchessWidth/16);
            firework[i].invalidate();
        }

        Random random = new Random();

        for (int i = 1; i <= 20; i ++) {
            float x_offset = random.nextFloat();
            float y_offset = random.nextFloat();
            x_offset = x_offset*2 - 1f;
            y_offset = y_offset*2 - 1f;
            fireworkanimation[i] = new TranslateAnimation(0, x_offset*smallchessWidth, 0, y_offset*smallchessWidth);
            fireworkanimation[i].setDuration(time);
            firework[i].setImageResource(R.color.chessboardbackground);
            if (i == 1) {
                fireworkanimation[i].setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        for (int i = 1; i <= 20; i ++) {
                            firework[i].setImageResource(R.color.transparent);
                        }
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
            }
            firework[i].startAnimation(fireworkanimation[i]);
        }

    }

    //EventBus.getDefault().post(0)z

    /*
    private int movefrompendingtochessboard = 1;
    private int movefromchessboardtopending = 2;
    private void movebetweenpendingandchessboard(int type) {
        if(type == movefrompendingtochessboard) {
            pending.startAnimation(transite[nowx*4 - 4 + nowy]);
        } else {
            imageViewchessboard.startAnimatio(reversetransite[nowx*4 - 4 + nowy]);
        }
    }


    // ViewGroup --> FireworkSystem
    private firework fireworkexample;
    private void initfireworksystem() {
        fireworkexample = new firework();
    }
    private class firework {
        private ViewGroup fireworksystem[][] = new ViewGroup[10][10];
        public firework() {
            for(int i = 1; i <= 4; i ++) {
                for(int j = 1; j <= 4; j ++) {
                    fireworksystem[i][j].addview();
                }
            }
        }
        public void shot(int type) {
            int x = nowx;
            int y = nowy;
        }
    }
    */

    /*
        final ValueAnimator valueAnimator = ValueAnimator.ofObject(new PointEvaluator(), new Point(50f, 50f), new Point(1000f, 1000f), new Point(500f, 500f));
        valueAnimator.setDuration(3000);

        valueAnimator.setInterpolator(new Interpolator() {
            @Override
            public float getInterpolation(float input) {
                return input/2;
            }
        });

        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                Point point = (Point)animation.getAnimatedValue();
                circle.x = point.getX();
                circle.y = point.getY();
                circle.invalidate();
            }
        });
        valueAnimator.start();
        Point point = new Point(50f, 50f);
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(circle, "x", 50f, 1000f, 500f);
        objectAnimator.setDuration(3000);
        objectAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                // float x = (Float) animation.getAnimatedValue();
                // circle.x = x;
                circle.invalidate();
            }
        });
        // objectAnimator.start();
        */
}

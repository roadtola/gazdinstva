package com.ebookfrenzy.gazdinstvaa;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;

import com.ebookfrenzy.gazdinstvaa.model.Company;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;

import org.jetbrains.annotations.NotNull;

public class MarkerClusterRenderer extends DefaultClusterRenderer<Company> {
    private Context context;

    public MarkerClusterRenderer(Context context, GoogleMap map,
                                 ClusterManager<Company> clusterManager) {
        super(context, map, clusterManager);
        this.context = context;
    }

    @Override
    protected void onBeforeClusterItemRendered(Company item, MarkerOptions markerOptions) {
        // use this to make your change to the marker option
        // for the marker before it gets render on the map
        Drawable myDrawable = context.getResources().getDrawable(R.drawable.grain);
        Bitmap myLogo = ((BitmapDrawable) myDrawable).getBitmap();
        //get width based on length of company name
        Bitmap myLogo1;
        if(item.getCompanyName().length() > 20){
            myLogo1 = Bitmap.createScaledBitmap(myLogo, 1000, 200, true);
        }else if(item.getCompanyName().length() > 15){
            myLogo1 = Bitmap.createScaledBitmap(myLogo, 800, 200, true);
        } else if (item.getCompanyName().length() > 10) {
            myLogo1 = Bitmap.createScaledBitmap(myLogo, 500, 200, true);
        }else {
            myLogo1 = Bitmap.createScaledBitmap(myLogo, 400, 200, true);
        }
        Bitmap bitmap = Bitmap.createBitmap(myLogo1.getWidth(),myLogo1.getHeight() + 50,Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas();
        float scale = context.getResources().getDisplayMetrics().density;
        canvas.setBitmap(bitmap);
        Drawable vectorDrawable = context.getResources().getDrawable(R.drawable.ic_wheat);
        vectorDrawable.setBounds(0, 50, 200, canvas.getHeight() - 50);
        vectorDrawable.draw(canvas);
        Paint paint = new Paint();
        paint.setColor(Color.TRANSPARENT);
        canvas.drawPaint(paint);
        paint.setColor(Color.BLACK);
        paint.setTextSize(15 * scale);
        int xPos = (canvas.getWidth() / 2) - (int)(paint.measureText(item.getCompanyName())/2);
        int yPos = (int) ((canvas.getHeight() / 2) - ((paint.descent() + paint.ascent()) / 2)) ;
        canvas.drawText(item.getCompanyName(),xPos,50,paint);
        canvas.save();
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(bitmap));
        markerOptions.draggable(true);

    }

}
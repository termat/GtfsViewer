package net.termat.gtfsviewer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import net.termat.gtfsviewer.components.RecycleGtfsRtListAdapter;
import net.termat.gtfsviewer.gtfs.data.GtfsView;
import net.termat.gtfsviewer.task.DownloadTask;
import net.termat.gtfsviewer.task.GtfsDeleteTask;

public class MainActivity extends AppCompatActivity {
    private RecyclerView listView;
    private RecycleGtfsRtListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LayoutInflater layoutInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        listView =(RecyclerView)findViewById(R.id.recycle_view_gtfs);
        LinearLayoutManager llm = new LinearLayoutManager(getThis());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        listView.setLayoutManager(llm);
        listView.setScrollBarStyle(ListView.SCROLLBARS_OUTSIDE_OVERLAY);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(listView.getContext(),
                new LinearLayoutManager(getThis()).getOrientation());
        listView.addItemDecoration(dividerItemDecoration);
        adapter=new RecycleGtfsRtListAdapter(getThis(),R.layout.item_text);
        listView.setAdapter(adapter);
        adapter.setOnItemClickListener(new RecycleGtfsRtListAdapter.OnClickListsner(){
            @Override
            public void onClick(GtfsView item) {
                View view = layoutInflater.inflate( R.layout.dialog_add_gtfs, null );
                final EditText name = (EditText) view.findViewById(R.id.EditText_dialog_name);
                name.setText(item.name);
                final EditText url_static = (EditText) view.findViewById(R.id.EditText_dialog_url_st);
                url_static.setText(item.gtfs_url);
                url_static.setEnabled(false);
                final EditText url_trip = (EditText) view.findViewById(R.id.EditText_dialog_url_tr);
                url_trip.setText(item.trip_url);
                final EditText url_vhei = (EditText) view.findViewById(R.id.EditText_dialog_url_vh);
                url_vhei.setText(item.vehicle_url);
                AlertDialog.Builder builder=new AlertDialog.Builder(getThis());
                builder.setTitle(getString(R.string.update_gtfs));
                builder.setView(view);
                DialogInterface.OnClickListener yes= (dialog, which) -> {
                    item.name=name.getText().toString();
                    item.trip_url =url_trip.getText().toString();
                    item.vehicle_url =url_vhei.getText().toString();
                    adapter.update(item);
                    listView.setAdapter(adapter);
                    dialog.dismiss();
                };
                builder.setPositiveButton(getString(R.string.redit), yes);
                DialogInterface.OnClickListener no= (dialog, which) -> {
                    dialog.dismiss();
                };
                builder.setNegativeButton(getString(R.string.cancel), no);
                builder.create().show();
            }
        });
        adapter.setOnExecClickListener(new RecycleGtfsRtListAdapter.OnClickListsner() {
            @Override
            public void onClick(GtfsView item) {
                goMap(item);
            }
        });
        final Drawable deleteIcon = getThis().getDrawable(R.drawable.icon_delete);
        ItemTouchHelper.SimpleCallback callback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove (@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
                return false;
            }
            @Override
            public void onSwiped (@NonNull RecyclerView.ViewHolder viewHolder, int i) {
                RecycleGtfsRtListAdapter adp=(RecycleGtfsRtListAdapter)listView.getAdapter();
                int index=viewHolder.getAdapterPosition();
                GtfsView g=adapter.get(index);
                AlertDialog.Builder builder=new AlertDialog.Builder(getThis());
                builder.setTitle(g.name+getString(R.string.remove_gtfs));
                DialogInterface.OnClickListener yes= (dialog, which) -> {
                    adapter.delete(g);
                    listView.setAdapter(adapter);
                    GtfsDeleteTask task = new GtfsDeleteTask(getApplicationContext(),g);
                    task.execute("");
                    dialog.dismiss();
                };
                builder.setPositiveButton(getString(R.string.yes), yes);
                DialogInterface.OnClickListener no= (dialog, which) -> {
                    listView.setAdapter(adapter);
                    dialog.dismiss();
                };
                builder.setNegativeButton(getString(R.string.cancel), no);
                AlertDialog d=builder.create();
                d.setCancelable(false);
                d.show();
            }
            @Override
            public void onChildDraw (@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                View itemView = viewHolder.itemView;
                if (dX == 0f && !isCurrentlyActive) {
                    clearCanvas(c, itemView.getRight() + (int) dX, itemView.getTop(), itemView.getRight(), itemView.getBottom());
                    super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, false);
                    return;
                }
                ColorDrawable background = new ColorDrawable();
                background .setColor(Color.parseColor("#ff7f50"));
                background.setBounds(itemView.getLeft(), itemView.getTop(),  itemView.getLeft()+ (int)dX, itemView.getBottom());
                background.draw(c);
                int deleteIconTop = itemView.getTop() + (itemView.getHeight() - deleteIcon.getIntrinsicHeight()) / 2;
                int deleteIconMargin = (itemView.getHeight() - deleteIcon.getIntrinsicHeight()) / 2;
                int deleteIconLeft = deleteIconMargin;
                int deleteIconRight = deleteIconMargin+deleteIcon.getIntrinsicWidth();
                int deleteIconBottom = deleteIconTop +  deleteIcon.getIntrinsicHeight();
                deleteIcon.setBounds(deleteIconLeft, deleteIconTop, deleteIconRight, deleteIconBottom);
                deleteIcon.draw(c);
            }
        };
        new ItemTouchHelper(callback).attachToRecyclerView(listView);
        Button bt0=(Button)findViewById(R.id.button_add_gtfs);
        bt0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddDialog(null);
            }
        });
    }

    private Activity getThis(){
        return this;
    }
    public void updateListView(){
        adapter.updateList();
        listView.setAdapter(adapter);
    }

    private void goMap(GtfsView g){
        Intent intent = new Intent(getThis(), MapActivity.class);
        intent.putExtra("DATA_ID",g.data_id);
        startActivity(intent);
   }

    private static void clearCanvas(Canvas c, int left, int top, int right, int bottom) {
        Paint paint = new Paint();
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        c.drawRect(left, top, right, bottom, paint);
    }

    private void showAddDialog(String[] str){
        LayoutInflater layoutInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view2 = layoutInflater.inflate( R.layout.dialog_add_gtfs, null );
        final EditText name = (EditText) view2.findViewById(R.id.EditText_dialog_name);
        final EditText url_static = (EditText) view2.findViewById(R.id.EditText_dialog_url_st);
        final EditText url_trip = (EditText) view2.findViewById(R.id.EditText_dialog_url_tr);
        final EditText url_vhei = (EditText) view2.findViewById(R.id.EditText_dialog_url_vh);
        if(str!=null){
            name.setText(str[1]);
            url_static.setText(str[2]);
            if(!str[3].equals("null"))url_trip.setText(str[3]);
            if(!str[4].equals("null"))url_vhei.setText(str[4]);
        }
        AlertDialog.Builder builder=new AlertDialog.Builder(getThis());
        builder.setTitle(getString(R.string.add_gtfs));
        builder.setView(view2);
        DialogInterface.OnClickListener yes= (dialog, which) -> {
            GtfsView gt=new GtfsView();
            gt.name=name.getText().toString();
            gt.gtfs_url =url_static.getText().toString();
            gt.trip_url =url_trip.getText().toString();
            gt.vehicle_url =url_vhei.getText().toString();
            if(gt.name==null||gt.name.isEmpty()||gt.gtfs_url==null||gt.gtfs_url.isEmpty()){
                AlertDialog.Builder b2=new AlertDialog.Builder(getThis());
                b2.setMessage("名称等が入力されていません。");
                b2.create().show();
            }else {
                Log.w("TEST-1",gt.name);
                Log.w("TEST-1",gt.gtfs_url);
                Log.w("TEST-1",gt.trip_url);
                Log.w("TEST-1",gt.vehicle_url);

                listView.setAdapter(adapter);
                DownloadTask task = new DownloadTask ((MainActivity) getThis(), getSupportFragmentManager(), gt);
                task.execute("");
                dialog.dismiss();
            }
        };
        builder.setPositiveButton(getString(R.string.redit), yes);
        DialogInterface.OnClickListener no= (dialog, which) -> {
            dialog.dismiss();
        };
        builder.setNegativeButton(getString(R.string.cancel), no);
        DialogInterface.OnClickListener scan= (dialog, which) -> {
            IntentIntegrator intentIntegrator = new IntentIntegrator(getThis());
            intentIntegrator.setCaptureActivity(QRScanner.class);
            intentIntegrator.setPrompt("Scan QR Code");
            intentIntegrator.setOrientationLocked(true);
            intentIntegrator.initiateScan();
        };
        builder.setNeutralButton(getString(R.string.qr_code),scan);
        builder.create().show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (intentResult != null) {
            if (intentResult.getContents() == null) {
                Toast.makeText(getBaseContext(), "Cancelled", Toast.LENGTH_SHORT).show();
            } else {
                String[] str=intentResult.getContents().split(",");
                showAddDialog(str);
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
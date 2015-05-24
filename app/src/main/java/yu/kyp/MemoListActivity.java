package yu.kyp;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import yu.kyp.common.Utils;
import yu.kyp.common.activity.ActivityBase;
import yu.kyp.image.Note;
import yu.kyp.image.NoteManager;


public class MemoListActivity extends ActivityBase {

    private static final String TAG = MemoListActivity.class.getSimpleName();
    private static final int REQUEST_WRITE_BG = 5;
    private NoteManager noteManager = null;
    private NoteManager noteManager2 = null;
    private Context context = null;
    private static SharedPreferences sp;
    RelativeLayout memoListRelativeLayout;
    static SharedPreferences sp2;
    private static SharedPreferences sp3;
    static int theme_num =7;
    private ListCursorAdapter adapterlist = null;
    private ListView ListViewNote;

    private static SharedPreferences for_alpha;
    private static SharedPreferences memo_title;
    Cursor c;
    Cursor for_thumbnail;
    private Note note;
    private Context mContext;
    static int memo_theme_number = 3;
    private ImageAdapter adapterGridNote = null;
    GridView memolist_gridview;
//    ListView listviewNote;

    private AdapterView.OnItemClickListener listenerListNote = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Toast.makeText(MemoListActivity.this,"메모 하나 눌렸당 " + position + "," + id,Toast.LENGTH_SHORT).show();
            sp = getSharedPreferences("current_p_size",MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.putInt("p_size_value",2);
            editor.commit();

            sp2 = getSharedPreferences("current_e_size",MODE_PRIVATE);
            SharedPreferences.Editor editor2 = sp2.edit();
            editor2.putInt("e_size_value",2);
            editor2.commit();

            Intent i = new Intent(context,MemoWriteActivity2.class);
            i.putExtra("NOTE_NO", (int) id);
            startActivity(i);
        }
    };
//    private SimpleCursorAdapter adapterListNote = null;
    private DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener()
    {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch(which)
            {
                case DialogInterface.BUTTON_POSITIVE:
                    ContentValues values = new ContentValues();
                    values.put("IS_DEL","1");
                    int cnt = db.execUpdate("NOTE", values, "NOTE_NO=" + deleteId);
                    Log.i(TAG,"cnt:"+cnt);
                    bindNote();
                    break;
                case DialogInterface.BUTTON_NEGATIVE:
                    break;
            }
        }
    };

    private long deleteId;
    //메모리스트에서 메모를 롱클릭하였을 때
    private AdapterView.OnItemLongClickListener longClickListenerListNote = new AdapterView.OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            deleteId  = id;
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage("삭제하시겠습니까?").setPositiveButton("Yes", dialogClickListener)
                    .setNegativeButton("No", dialogClickListener).show();

            return true;
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memo_list);

        memoListRelativeLayout = (RelativeLayout)findViewById(R.id.memoListRelativeLayout);
        memoListRelativeLayout.setBackgroundColor(0xffffff);

        //memoListRelativeLayout.setBackground(getResources().getDrawable(R.drawable.background));

        context = this;
        noteManager = new NoteManager(this);
        noteManager2 = new NoteManager(this);

        // 2. ListView OnItemClickLIstener 설정
        ListViewNote = (ListView) findViewById(R.id.listViewNote);
        ListViewNote.setOnItemClickListener(listenerListNote);
        ListViewNote.setOnItemLongClickListener(longClickListenerListNote);

    }

    //검색 버튼이 눌렸을 때
    public void buttonSearch_OnClick(View v){
        Intent intent = new Intent(getApplicationContext(), SearchActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();

        sp3 = getSharedPreferences("setbackground", MODE_PRIVATE);

        //SettingsActivity에서 받은 Preferences값을 이용하여 테마 이미지 처리
        if(sp3.getInt("theme",0) == 1){
            memoListRelativeLayout.setBackground(getResources().getDrawable(R.drawable.cat2));
        }
        if(sp3.getInt("theme",0) == 2){
            memoListRelativeLayout.setBackground(getResources().getDrawable(R.drawable.background));
        }
        if(sp3.getInt("theme",0) == 3){

        }
        if(sp3.getInt("theme",0) == 4){

        }
        if(sp3.getInt("theme",0) == 5){

        }
        if(sp3.getInt("theme",0) == 6){

        }
        if(sp3.getInt("theme",0) == 7){

        }



        //         ListView에 노트 내용 뿌려주기.
        sp = getSharedPreferences("list_select", MODE_PRIVATE);
        memo_theme_number = sp.getInt("list_num",0);
        if (memo_theme_number == 1) {
            Toast.makeText(MemoListActivity.this,"리스트종류"+memo_theme_number,Toast.LENGTH_SHORT).show();
            //바둑판()


            memolist_gridview = (GridView) findViewById(R.id.memolist_gridview);

            ListViewNote.setVisibility(View.GONE);
            memolist_gridview.setVisibility(View.VISIBLE);

            gridviewbindNote();
            //memolist_gridview.setAdapter(adapterGridNote);

            //그리드뷰의 한 부분이 클릭되었을 때
            memolist_gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView parent, View v, int position, long id) {
                    Toast.makeText(MemoListActivity.this,"그리드뷰",Toast.LENGTH_SHORT).show();
                    sp = getSharedPreferences("current_p_size",MODE_PRIVATE);
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putInt("p_size_value",2);
                    editor.commit();

                    sp2 = getSharedPreferences("current_e_size",MODE_PRIVATE);
                    SharedPreferences.Editor editor2 = sp2.edit();
                    editor2.putInt("e_size_value",2);
                    editor2.commit();

                    for_alpha = getSharedPreferences("alpha_value",MODE_PRIVATE);
                    SharedPreferences.Editor editor3 = for_alpha.edit();
                    editor3.putInt("alpha_value_is",255);
                    editor3.commit();


                    Intent i = new Intent(context,MemoWriteActivity2.class);
                    i.putExtra("NOTE_NO", (int) id);
                    startActivity(i);
                }
            });

        }
        else if (memo_theme_number == 2){
            //타임라인 형식으로 리스트 생성
            Toast.makeText(MemoListActivity.this,"리스트종류"+memo_theme_number,Toast.LENGTH_SHORT).show();
        }
        else if (memo_theme_number == 3){
            Toast.makeText(MemoListActivity.this,"리스트종류"+memo_theme_number,Toast.LENGTH_SHORT).show();

            memolist_gridview = (GridView) findViewById(R.id.memolist_gridview);

            //리스트 형식으로 리스트 생성(기본 형태)
            ListViewNote.setVisibility(View.VISIBLE);
            memolist_gridview.setVisibility(View.GONE);
            bindNote();
//            listviewNote.setVisibility(View.GONE);

        }
        Log.d(TAG, "onResume");
        Log.d(TAG, "Settings.getDefaultFactor():" + settings.getDefaultFactor());
        Log.i(TAG,"Setting.getFontType():"+settings.getFontType());
        Log.i(TAG,"Setting.getZoomFactor():"+settings.getZoomFactor());
        Log.i(TAG, "Setting.getBackgroundType():" + settings.getBackgroundType());
        Log.i(TAG, "Setting.getAlarmType():" + settings.getAlarmType());
        Log.i(TAG, "Setting.getListType():" + settings.getListType());
    }

    /**
     * ListView에 노트 내용 뿌려주기.
     */
    private void bindNote() {
        Cursor c = noteManager.getNoteList();
        if(adapterlist==null) {
//            adapterListNote = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_2, c, new String[]{"TITLE", "LAST_MOD_DT"}, new int[]{android.R.id.text1, android.R.id.text2});
//            ListView ListViewNote = (ListView) findViewById(R.id.listViewNote);
//            ListViewNote.setAdapter(adapterListNote);
            adapterlist = new ListCursorAdapter(this,c);
            if(adapterlist!=null)
                ListViewNote.setAdapter(adapterlist);
        }
        else
        {
            adapterlist.changeCursor(c);
        }

    }

    /**
     * GridView에 노트 내용 뿌려주기.
     */
    private void gridviewbindNote() {
        for_thumbnail = noteManager2.getNoteList();


        while (for_thumbnail.moveToNext()) {
            int noteNo = for_thumbnail.getInt(for_thumbnail.getColumnIndex("_id"));
            String temp = for_thumbnail.getString(for_thumbnail.getColumnIndex("THUM_NO"));
            int thumNo = for_thumbnail.getInt(for_thumbnail.getColumnIndex("THUM_NO"));
            Log.e(TAG, "thumNo:" + thumNo + " noteNo:" + noteNo + " temp:" + temp);
//            adapterGridNote = new ImageAdapter(this,for_thumbnail);
//            memolist_gridview.setAdapter(adapterGridNote);
        }
        for_thumbnail.moveToFirst();
        if(adapterGridNote==null) {
            adapterGridNote = new ImageAdapter(this,for_thumbnail);
            memolist_gridview.setAdapter(adapterGridNote);
            memolist_gridview.setPadding(10,20,0,20);
//            Log.e("썸네일 개수!!!", "" + for_thumbnail.getCount());
        }
        else
        {
            adapterGridNote.changeCursor(for_thumbnail);
        }
    }



    //그리드뷰로 이미지를 처리하기 위한 부분
    public class ImageAdapter extends CursorAdapter {
        public ImageAdapter(Context context, Cursor cursor) {
            super(context, cursor, 0);
            noteManager2 = new NoteManager(context);
        }



        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            LayoutInflater inflater = LayoutInflater.from(context);
            View v = inflater.inflate(R.layout.image_view, parent, false);
            return v;

        }

        /**
         * 그리드뷰에 썸네일 뿌려주기
         * @param view
         * @param context
         * @param cursor
         */
        // 1.노트 목록 가져오기 Cursor c = noteManager.getNoteList();
        // 2.커서어뎁터에 set
        // 3.bindView에서 blob->Bitmap 으로 변환. Utils.getImage()
        @Override
        public void bindView(View view, Context context, Cursor cursor) {
//            memolist_gridview = (GridView) findViewById(R.id.memolist_gridview);
            ImageView iv = (ImageView) view.findViewById(R.id.for_thumbnail);
            iv.setPadding(0,10,20,10);

            Bitmap grid_image = Utils.getImage(cursor.getBlob(cursor.getColumnIndex("THUM_DATA")));
            Log.e("썸네일 데이터 확인", "" + for_thumbnail.getBlob(for_thumbnail.getColumnIndex("THUM_DATA")));
            iv.setImageBitmap(grid_image);

//            Bitmap grid_image = Utils.getImage(cursor.getBlob(cursor.getColumnIndex("THUM_DATA")));
//            Log.e("썸네일 데이터 확인", "" + for_thumbnail.getBlob(for_thumbnail.getColumnIndex("THUM_DATA")));
//            viewHolder.iv.setImageBitmap(grid_image);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_memo_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void buttonNewMemo_OnClick(View v)
    {
        Intent i = new Intent(context,WriteBackgoundSelectActivity.class);
        startActivityForResult(i, REQUEST_WRITE_BG);
    }



    public void buttonSelect_OnClick(View v)
    {
        startActivity(new Intent(this,SelectActivity.class));
    }


    public void buttonTrash_OnClick(View v)
    {
        startActivity(new Intent(this,TrashActivity.class));
    }

    public void buttonSetting_OnClick(View v)
    {
        startActivity(new Intent(this, SettingsActivity.class));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        Log.e(TAG,"requestCode:"+requestCode+" resultCode:"+resultCode);
        if(requestCode==REQUEST_WRITE_BG)
        {
            if(resultCode == RESULT_OK)
            {
                if(data!=null)
                {
                    // 1. 결과값으로 배경 종류를 받는다. (0:라인 1:무지 2:회의록)
                    int position = data.getIntExtra("position",0);
                    Intent i = new Intent(context,MemoWriteActivity2.class);
                    i.putExtra("bg_type",position);
                    startActivity(i);


                }
            }
        }
    }
}


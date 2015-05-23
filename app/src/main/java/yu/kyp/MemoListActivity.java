package yu.kyp;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleCursorAdapter;

import yu.kyp.common.activity.ActivityBase;
import yu.kyp.image.NoteManager;


public class MemoListActivity extends ActivityBase {

    private static final String TAG = MemoListActivity.class.getSimpleName();
    private static final int REQUEST_WRITE_BG = 5;
    private NoteManager noteManager = null;
    private Context context = null;
    private static SharedPreferences sp;
    RelativeLayout memoListRelativeLayout;
    static SharedPreferences sp2;
    private static SharedPreferences sp3;
    static int theme_num =7;
    private AdapterView.OnItemClickListener listenerListNote = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            sp = getSharedPreferences("current_p_size",MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.putInt("p_size_value",2);
            editor.commit();

            sp2 = getSharedPreferences("current_e_size",MODE_PRIVATE);
            SharedPreferences.Editor editor2 = sp2.edit();
            editor2.putInt("e_size_value",2);
            editor2.commit();

            Intent i = new Intent(context,MemoWriteActivity2.class);
            i.putExtra("NOTE_NO",(int)id);
            startActivity(i);
        }
    };
    private SimpleCursorAdapter adapterListNote = null;
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

        // 1. 변수 준비
        memoListRelativeLayout = (RelativeLayout)findViewById(R.id.memoListRelativeLayout);
        memoListRelativeLayout.setBackgroundColor(0xffffff);
        context = this;
        noteManager = new NoteManager(this);

        // 2. ListView OnItemClickLIstener 설정
        ListView listviewNote = (ListView) findViewById(R.id.listViewNote);
        listviewNote.setOnItemClickListener(listenerListNote);
        listviewNote.setOnItemLongClickListener(longClickListenerListNote);



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



        // ListView에 노트 내용 뿌려주기.
        bindNote();
        Log.d(TAG, "onResume");
        Log.d(TAG, "Settings.getDefaultFactor():" + settings.getDefaultFactor());
        Log.i(TAG,"Setting.getFontType():"+settings.getFontType());
        Log.i(TAG,"Setting.getZoomFactor():"+settings.getZoomFactor());
        Log.i(TAG,"Setting.getBackgroundType():"+settings.getBackgroundType());
        Log.i(TAG,"Setting.getAlarmType():"+settings.getAlarmType());
        Log.i(TAG,"Setting.getListType():"+settings.getListType());
    }

    /**
     * ListView에 노트 내용 뿌려주기.
     */
    private void bindNote() {
        Cursor c = noteManager.getNoteList();
        if(adapterListNote==null) {
            adapterListNote = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_2, c, new String[]{"TITLE", "LAST_MOD_DT"}, new int[]{android.R.id.text1, android.R.id.text2});
            ListView listviewNote = (ListView) findViewById(R.id.listViewNote);
            
            listviewNote.setAdapter(adapterListNote);
        }
        else
        {
            adapterListNote.changeCursor(c);
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
        startActivity(new Intent(this,SettingsActivity.class));
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


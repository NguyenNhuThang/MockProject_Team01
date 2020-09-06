package gst.trainingcourse.mockproject_team01.responsitory.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.Date;

import gst.trainingcourse.mockproject_team01.model.LessonSchedule;
import gst.trainingcourse.mockproject_team01.model.Subject;
import gst.trainingcourse.mockproject_team01.model.WeekSchedule;

public class AppDatabaseHelper extends SQLiteOpenHelper {

    public final static String DATABASE_NAME = "ScheduleDatabase";
    public final static int DATABASE_VERSION = 1;

    // table subject
    public final static String SUBJECT_TABLE_NAME = "tblSubject";
    public final static String SUBJECT_ID = "id";
    public final static String SUBJECT_NAME = "name";

    // table subject schedule
    public final static String LESSON_SCHEDULE_TABLE_NAME = "tblSubjectSchedule";
    public final static String LESSON_SCHEDULE_ID = "id";
    public final static String LESSON_SCHEDULE_SUBJECT_ID = "subject_id";
    public final static String LESSON_SCHEDULE_SUBJECT_TIME_ID = "subject_time_id";
    public final static String LESSON_SCHEDULE_START_TIME = "start_date";
    public final static String LESSON_SCHEDULE_END_TIME = "end_date";
    public final static String LESSON_SCHEDULE_DAY = "day";
    public final static String LESSON_SCHEDULE_LESSON = "lesson";


    private static AppDatabaseHelper mInstance;

    private AppDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static AppDatabaseHelper getInstance(Context context) {
        if (mInstance == null) mInstance = new AppDatabaseHelper(context);
        return mInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String createSubjectTable = "CREATE TABLE " + SUBJECT_TABLE_NAME + " (" + SUBJECT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + SUBJECT_NAME + " TEXT)";
        String createScheduleTable = "CREATE TABLE " + LESSON_SCHEDULE_TABLE_NAME + " (" + LESSON_SCHEDULE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + LESSON_SCHEDULE_SUBJECT_ID + " INTEGER, "
                + LESSON_SCHEDULE_SUBJECT_TIME_ID + " INTEGER, "
                + LESSON_SCHEDULE_START_TIME + " INTEGER, "
                + LESSON_SCHEDULE_END_TIME + " INTEGER, "
                + LESSON_SCHEDULE_DAY + " INTEGER, "
                + LESSON_SCHEDULE_LESSON + " INTEGER)";
        sqLiteDatabase.execSQL(createSubjectTable);
        sqLiteDatabase.execSQL(createScheduleTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        String dropSubjectTable = "DROP TABLE IF EXISTS " + SUBJECT_TABLE_NAME;
        String dropScheduleTable = "DROP TABLE IF EXISTS " + LESSON_SCHEDULE_TABLE_NAME;

        sqLiteDatabase.execSQL(dropSubjectTable);
        sqLiteDatabase.execSQL(dropScheduleTable);
    }

    public WeekSchedule getWeekSchedule(Date[] dates) {
        return new WeekSchedule(dates, getSchedule(dates));
    }

    public ArrayList<LessonSchedule> getSchedule(Date[] dates) {
        long startTime = dates[0].getTime();
        long endTime = dates[1].getTime();

        String sql = "SELECT * FROM " + LESSON_SCHEDULE_TABLE_NAME + " where " + LESSON_SCHEDULE_START_TIME + " >= " + startTime
                + " AND " + LESSON_SCHEDULE_END_TIME + " < " + endTime;
        SQLiteDatabase db = getReadableDatabase();
        ArrayList<LessonSchedule> listSchedule = new ArrayList<>();

        long id;
        int subId, day, lesson;
        long sTime, eTime;
        Subject subject;

        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToNext()) {
            do {
                try {
                    id = cursor.getInt(cursor.getColumnIndex(LESSON_SCHEDULE_ID));
                    subId = cursor.getInt(cursor.getColumnIndex(LESSON_SCHEDULE_SUBJECT_ID));
                    sTime = cursor.getLong(cursor.getColumnIndex(LESSON_SCHEDULE_START_TIME));
                    eTime = cursor.getLong(cursor.getColumnIndex(LESSON_SCHEDULE_END_TIME));
                    day = cursor.getInt(cursor.getColumnIndex(LESSON_SCHEDULE_DAY));
                    lesson = cursor.getInt(cursor.getColumnIndex(LESSON_SCHEDULE_LESSON));

                    subject = getSuject(subId);

                    listSchedule.add(new LessonSchedule(id, new Date(sTime), new Date(eTime), subject, day, lesson));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } while (cursor.moveToNext());
        }
        return listSchedule;
    }

    public Subject getSuject(long id) {
        SQLiteDatabase db = getReadableDatabase();
        String sql = "SELECT * FROM " + SUBJECT_TABLE_NAME + " WHERE " + SUBJECT_ID + " = " + id;
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToNext()) {
            String subName = cursor.getString(cursor.getColumnIndex(SUBJECT_NAME));
            return new Subject(id, subName);
        }
        return null;
    }

    public void insertSubject(Subject subject) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(SUBJECT_ID, subject.getId());
        values.put(SUBJECT_NAME, subject.getName());
        db.insert(SUBJECT_TABLE_NAME, null, values);
        db.close();
    }

    public void insertLessionSchedule(LessonSchedule schedule) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(LESSON_SCHEDULE_ID, schedule.getId());
        values.put(LESSON_SCHEDULE_SUBJECT_ID, schedule.getSubject().getId());
        values.put(LESSON_SCHEDULE_START_TIME, schedule.getStartDate().getTime());
        values.put(LESSON_SCHEDULE_END_TIME, schedule.getEndDate().getTime());
        values.put(LESSON_SCHEDULE_DAY, schedule.getDay());
        values.put(LESSON_SCHEDULE_LESSON, schedule.getLesson());
        db.insert(LESSON_SCHEDULE_TABLE_NAME, null, values);
        db.close();
    }

    public ArrayList<Subject> getAllSubjects() {
        SQLiteDatabase db = getReadableDatabase();
        String sql = "SELECT * FROM " + SUBJECT_TABLE_NAME;
        ArrayList<Subject> list = new ArrayList<>();
        long id;
        String name;
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToNext()) {
            do {
                id = cursor.getInt(cursor.getColumnIndex(SUBJECT_ID));
                name = cursor.getString(cursor.getColumnIndex(SUBJECT_NAME));
                list.add(new Subject(id, name));
            } while (cursor.moveToNext());
        }
        return list;
    }

}

package com.example.backbone

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import java.sql.ResultSet

//sql문으로 DB 연결시켜주는 클래스

//Backbone.db 파일을 찾도록 하고 없으면 새로 생성시켜주기.
class DBHelper(context: Context): SQLiteOpenHelper(context, "Backbone.db", null, 1) {
    override fun onCreate(db: SQLiteDatabase?) {
    //추후 수정 예정
        /*
                Log.d("태그", "실행되냐")
     //대답 테이블
        db!!.execSQL("CREATE TABLE Answer (QustionID TEXT NOT NULL,AnswerID INTEGER NOT NULL,Content TEXT,PRIMARY KEY(AnswerID));")

        //글 테이블
        db!!.execSQL("CREATE TABLE \"Writing\" (\n" +
                "\t\"WriteID\"\tINTEGER,\n" +
                "\t\"Content\"\tTEXT,\n" +
                "\t\"Title\"\tTEXT NOT NULL,\n" +
                "\t\"Date\"\tTEXT NOT NULL,\n" +
                "\t\"Category\"\tTEXT NOT NULL,\n" +
                "\tPRIMARY KEY(\"WriteID\" AUTOINCREMENT)\n" +
                ")")

        //글 테이블
        db!!.execSQL("CREATE TABLE \"Question\" (\n" +
                "                \"WritingID\"\tTEXT,\n" +
                "                \"ContentID\"\tTEXT NOT NULL,\n" +
                "                \"QuestionID\"\tINTEGER NOT NULL UNIQUE,\n" +
                "                \"Content\"\tTEXT NOT NULL,\n" +
                "                \"Image\"\tBLOB,\n" +
                "                PRIMARY KEY(\"QuestionID\" AUTOINCREMENT)\n" +
                "        );")


         */


    }

    //버전을 업그레이드 하면 실행 -> 기존에 있던 테이블을 삭제한 후 실행.
    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        //DB 삭제 후 다시 생성
        db!!.execSQL("DROP TABLE IF EXISTS Backbone")
        onCreate(db)
    }

    //홈 화면
    //글 객체에 제목, 카테고리 이름, DATE, 해당 글에 저장된 Question갯수 출력
    fun getWriting(): Array<Writing>
    {
        //db읽어올 준비
        var db = this.readableDatabase

        var cursor2: Cursor

        var anyArray = arrayOf<Writing>()

        var cursor: Cursor = db.rawQuery("SELECT*FROM Writing;", null)
        //결과값이 끝날 때 까지 - 글 객체 생성한 뒤, 해당 객체 내용 띄우기
        while (cursor.moveToNext()) {
            //빈 객체 생성
            var writing:Writing = Writing()

            writing.WriteID = cursor.getInt(0)
            writing.content = cursor.getString(1)
            writing.Title =  cursor.getString(2)
            writing.Date =  cursor.getString(3)
            writing.Category =  cursor.getString(4)

            cursor2 =db.rawQuery("SELECT COUNT(*) FROM Question WHERE WritingID = '${writing.WriteID}';", null)
            while(cursor2.moveToNext())
            {
                writing.Question = cursor2.getInt(0)
            }

            anyArray+=writing

        }


        return anyArray

        // 디비 닫기
        db.close()
    }
    
    // 잠금 화면
    // 사용자에게 비밀번호 관리하는 DB 있는지 Boolean으로 반환하는 함수
    //User 테이블이 존재하면 true반환 없으면 false 반환
    fun PWisExist()
    {
        var db= this.readableDatabase

        var cursor: Cursor = db.rawQuery("SELECT EXISTS( SELECT 1 FROM User)As flag;", null)

        // 디비 닫기
        db.close()
    }
    
    //잠금 화면
    //사용자 User 테이블에 저장되어있는 사용자 디바이스 비밀번호 정보 불러오기
    fun getUserPassWord(): String
    {
        var db = this.readableDatabase

        var cursor: Cursor = db.rawQuery("SELECT*FROM USER;", null)

        cursor.moveToFirst()

        var Userpw: String = cursor.getString(0)

        return Userpw
    }

    //카테고리 화면
    //BottomFragmentList.kt
    //카테고리 내용 받아오기
    fun getCategory(): ArrayList<String>
    {
        //db읽어올 준비
        var db = this.readableDatabase

        var anyArray = ArrayList<String>()

        var cursor: Cursor = db.rawQuery("SELECT*FROM Category;", null)
        //결과값이 끝날 때 까지 - 글 객체 생성한 뒤, 해당 객체 내용 띄우기
        while (cursor.moveToNext()) {
            var category:String = cursor.getString(0)
            anyArray.add(category)

        }


        return anyArray

        // 디비 닫기
        db.close()
    }

    //카테고리 화면
    //BottomFragmentAdd.kt
    //카테고리 추가하기
    fun addCategory(category: String)
    {
        var db = this.writableDatabase
        db.execSQL("INSERT INTO Category VALUES ('" + category + "');")

        db.close()
    }

    //질문 리스트 화면
    //MyQuestionActivity.kt
    //카테고리 내용 받아오기
    fun getQuestion(): ArrayList<Question>
    {
        //db읽어올 준비
        var db = this.readableDatabase

        var anyArray = ArrayList<Question>()

        var cursor: Cursor = db.rawQuery("SELECT*FROM Question;", null)
        //결과값이 끝날 때 까지 - 글 객체 생성한 뒤, 해당 객체 내용 띄우기
        while (cursor.moveToNext()) {
            //클래스 생성에 필요한 내용 받아오기
            var WritingID:String = cursor.getString(0)
            var ContentID:String = cursor.getString(1)
            var QuestionID: Int = cursor.getInt(2)
            var Content:String = cursor.getString(3)
            var q:Question = Question(WritingID, ContentID, QuestionID, Content)

            var cursor2:Cursor =db.rawQuery("SELECT*FROM Writing WHERE WriteID = ${q.WritingID};", null)
            while(cursor2.moveToNext())
            {
                q.WritingTitle = cursor2.getString(2)
            }

            anyArray.add(q)
        }

        return anyArray

        // 디비 닫기
        db.close()
    }
}
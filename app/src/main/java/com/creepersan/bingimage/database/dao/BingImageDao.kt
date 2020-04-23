package com.creepersan.bingimage.database.dao

import androidx.room.*
import com.creepersan.bingimage.database.COL_BINGIMAGE_ID
import com.creepersan.bingimage.database.TB_BINGIMAGE
import com.creepersan.bingimage.database.bean.BingImage
import java.util.ArrayList

@Dao
interface BingImageDao {

    @Query("SELECT * FROM $TB_BINGIMAGE ORDER BY $COL_BINGIMAGE_ID DESC")
    fun getAll():List<BingImage>

    @Query("SELECT * FROM $TB_BINGIMAGE WHERE $COL_BINGIMAGE_ID IN (:ids) ORDER BY $COL_BINGIMAGE_ID")
    fun get(ids: Array<Int>):List<BingImage>

    @Query("SELECT * FROM $TB_BINGIMAGE WHERE $COL_BINGIMAGE_ID = (:id)")
    fun get(id:Int):BingImage

    @Query("SELECT * FROM $TB_BINGIMAGE WHERE $COL_BINGIMAGE_ID > (:start) ORDER BY $COL_BINGIMAGE_ID DESC LIMIT (:length)")
    fun getOffset(start:Int=0, length:Int):List<BingImage>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun inserts(vararg bingImage: BingImage)

    @Update
    fun update(vararg bingImage: BingImage)

    @Delete
    fun deletes(vararg bingImage: BingImage)

}
package com.example.spacexdemo.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.spacexdemo.pojos.CrewListModel;

import java.util.List;

@Dao
public interface CrewDao {

    @Query("SELECT * FROM crew_data")
    LiveData<List<CrewListModel>> getAllCrewData();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAllCrewData(List<CrewListModel> crewList);

    @Update
    void updateImageData(CrewListModel crewListModel);

    @Query("DELETE FROM crew_data")
    void deleteAllCrewData();
}

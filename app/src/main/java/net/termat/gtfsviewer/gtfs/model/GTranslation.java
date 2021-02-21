package net.termat.gtfsviewer.gtfs.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class GTranslation {

	@PrimaryKey(autoGenerate = true)
    public Long id;
	public String translation;
	public String trans_id;
	public String lang;
	public String data_id;
}

# RebtelTest
<?xml version="1.0" encoding="utf-8"?>  
<layer-list xmlns:android="http://schemas.android.com/apk/res/android">  
    <item>  
        <!-- 倒三角 -->  
        <rotate  
            android:fromDegrees="45"  
            android:pivotX="135%"  
            android:pivotY="15%"  
            android:toDegrees="45">  
            <shape android:shape="rectangle">  
                <solid android:color="#f00" />  
            </shape>  
        </rotate>  
    </item>  
</layer-list>

<?xml version="1.0" encoding="utf-8"?>
<layer-list xmlns:android="http://schemas.android.com/apk/res/android">
    <!-- 倒三角 -->
    <item>
        <rotate
            android:fromDegrees="45"
            android:pivotX="135%"
            android:pivotY="15%">
            <shape android:shape="rectangle">
                <size
                    android:width="16dp"
                    android:height="16dp" />
                <solid android:color="#7d72ff" />
            </shape>
        </rotate>
    </item>
</layer-list>

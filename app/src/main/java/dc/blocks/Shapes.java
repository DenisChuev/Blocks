package dc.blocks;

public class Shapes {

    final static boolean[][][] VALUES = {

            //I
            {
                    {false, false, false, false},
                    {true, true, true, true},
                    {false, false, false, false},
                    {false, false, false, false},
            },

            //J
            {
                    {false, false, true},
                    {true, true, true},
                    {false, false, false},
            },

            //L
            {
                    {true, false, false},
                    {true, true, true},
                    {false, false, false},
            },

            //S
            {
                    {true, true, false},
                    {false, true, true},
                    {false, false, false},
            },

            //Z
            {
                    {false, true, true},
                    {true, true, false},
                    {false, false, false},
            },

            //T
            {
                    {false, true, false},
                    {true, true, true},
                    {false, false, false},
            },

            //O
            {
                    {true, true},
                    {true, true},
            },
    };

    final static String COLORS[] = {

            "#fafafa",   //Background
            "#00CED1",  //Cian color
            "#3a3aff",  //Blue color
            "#FFA500",  //Orange color
            "#32CD32",  //Green color
            "#dc1623",  //Red color
            "#802877",  //Purple color
            "#ffd700"   //Yellow color
    };

}
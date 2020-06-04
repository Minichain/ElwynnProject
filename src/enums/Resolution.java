package enums;

public enum Resolution {
    /**
     * Only resolutions with ratio 16:9 & 4:3, for now.
     * More will be added in the future.
     * **/
    RESOLUTION_640_480 (0),     //4:3
    RESOLUTION_854_480 (1),
    RESOLUTION_1280_720 (2),    //16:9, 720p, HD
    RESOLUTION_1024_768 (3),    //4:3
    RESOLUTION_1600_900 (4),
    RESOLUTION_1920_1080 (5),   //16:9, 1080p, Full HD
    RESOLUTION_2560_1440 (6),    //16:9
    RESOLUTION_3840_2160 (7);    //16:9, 4K

    int resolutionValue;

    Resolution(int resolutionValue) {
        this.resolutionValue = resolutionValue;
    }

    public int getResolutionValue() {
        return resolutionValue;
    }

    public static Resolution getResolution(int width, int height) {
        switch (height) {
            case 480:
                if (width == 640) {
                    return RESOLUTION_640_480;
                } else if (width == 854) {
                    return RESOLUTION_854_480;
                }
            case 720:
                return RESOLUTION_1280_720;
            case 768:
                return RESOLUTION_1024_768;
            case 900:
                return RESOLUTION_1600_900;
            case 1080:
            default:
                return RESOLUTION_1920_1080;
            case 1440:
                return RESOLUTION_2560_1440;
            case 2160:
                return RESOLUTION_3840_2160;
        }
    }

    public int[] getResolution() {
        switch (this) {
            case RESOLUTION_640_480:
                return new int[]{640, 480};
            case RESOLUTION_854_480:
                return new int[]{854, 480};
            case RESOLUTION_1280_720:
                return new int[]{1280, 720};
            case RESOLUTION_1024_768:
                return new int[]{1024, 768};
            case RESOLUTION_1600_900:
                return new int[]{1600, 900};
            case RESOLUTION_1920_1080:
            default:
                return new int[]{1920, 1080};
            case RESOLUTION_2560_1440:
                return new int[]{2560, 1440};
            case RESOLUTION_3840_2160:
                return new int[]{3840, 2160};
        }
    }

    public String toString() {
        switch (this) {
            case RESOLUTION_640_480:
                return "640x480";
            case RESOLUTION_854_480:
                return "854x480";
            case RESOLUTION_1280_720:
                return "1280x720";
            case RESOLUTION_1024_768:
                return "12024x720";
            case RESOLUTION_1600_900:
                return "1600x900";
            case RESOLUTION_1920_1080:
            default:
                return "1920x1080";
            case RESOLUTION_2560_1440:
                return "2560x1440";
            case RESOLUTION_3840_2160:
                return "3840x2160";
        }
    }
}

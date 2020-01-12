package main;

import org.lwjgl.opengl.*;

import static org.lwjgl.opengl.GL11.*;

public class MyOpenGL {

    /*
     * program shader, to which is attached a vertex and fragment shaders.
     * They are set to 0 as a check because GL will assign unique int
     * values to each
     */
    public static int programShader01 = 0;

    private static void loadShaders() {
        int vertShader;
        int fragShader;

        try {
            vertShader = createShader("res/shaders/shader01.vert", ARBVertexShader.GL_VERTEX_SHADER_ARB);
            fragShader = createShader("res/shaders/shader01.frag", ARBFragmentShader.GL_FRAGMENT_SHADER_ARB);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        if (vertShader == 0 || fragShader == 0) return;

        programShader01 = ARBShaderObjects.glCreateProgramObjectARB();

        if (programShader01 == 0) return;

        /*
         * if the vertex and fragment shaders setup sucessfully,
         * attach them to the shader program, link the sahder program
         * (into the GL context I suppose), and validate
         */
        ARBShaderObjects.glAttachObjectARB(programShader01, vertShader);
        ARBShaderObjects.glAttachObjectARB(programShader01, fragShader);

        ARBShaderObjects.glLinkProgramARB(programShader01);
        if (ARBShaderObjects.glGetObjectParameteriARB(programShader01, ARBShaderObjects.GL_OBJECT_LINK_STATUS_ARB) == GL11.GL_FALSE) {
            System.err.println(getLogInfo(programShader01));
            return;
        }

        ARBShaderObjects.glValidateProgramARB(programShader01);
        if (ARBShaderObjects.glGetObjectParameteriARB(programShader01, ARBShaderObjects.GL_OBJECT_VALIDATE_STATUS_ARB) == GL11.GL_FALSE) {
            System.err.println(getLogInfo(programShader01));
            return;
        }
    }

    public static void prepareOpenGL() {
        GL.createCapabilities();

        //load our shader program and sprite batch
        loadShaders();

        glEnable(GL_TEXTURE_2D);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
    }

    public static void prepareFrame() {
        glClear(GL_COLOR_BUFFER_BIT);
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        glOrtho(0, Parameters.getInstance().getWindowWidth(), Parameters.getInstance().getWindowHeight(), 0, 1, -1);
        glMatrixMode(GL_MODELVIEW);
        glLoadIdentity();
        glEnable(GL_BLEND);
    }

    public static void drawTexture(int x, int y, float u, float v, float u2, float v2, float spriteWidth, float spriteHeight) {
        glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        glTexCoord2f(u, v);
        glVertex2f(x, y);
        glTexCoord2f(u, v2);
        glVertex2f(x, y + spriteHeight);
        glTexCoord2f(u2, v2);
        glVertex2f(x + spriteWidth, y + spriteHeight);
        glTexCoord2f(u2, v);
        glVertex2f(x + spriteWidth, y);
    }

    public static void drawTexture(int x, int y, double u, double v, double u2, double v2, float spriteWidth, float spriteHeight) {
        glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        glTexCoord2d(u, v);
        glVertex2f(x, y);
        glTexCoord2d(u, v2);
        glVertex2f(x, y + spriteHeight);
        glTexCoord2d(u2, v2);
        glVertex2f(x + spriteWidth, y + spriteHeight);
        glTexCoord2d(u2, v);
        glVertex2f(x + spriteWidth, y);
    }

    public static void drawTextureAlpha(int x, int y, double u, double v, double u2, double v2, float spriteWidth, float spriteHeight, double transparency) {
        glColor4f(1.0f, 1.0f, 1.0f, (float) transparency);
        glTexCoord2d(u, v);
        glVertex2f(x, y);
        glTexCoord2d(u, v2);
        glVertex2f(x, y + spriteHeight);
        glTexCoord2d(u2, v2);
        glVertex2f(x + spriteWidth, y + spriteHeight);
        glTexCoord2d(u2, v);
        glVertex2f(x + spriteWidth, y);
    }

    /*
     * With the exception of syntax, setting up vertex and fragment shaders
     * is the same.
     * @param the name and path to the vertex shader
     */
    private static int createShader(String filename, int shaderType) throws Exception {
        int shader = 0;
        try {
            shader = ARBShaderObjects.glCreateShaderObjectARB(shaderType);

            if (shader == 0) return 0;

            ARBShaderObjects.glShaderSourceARB(shader, Utils.readFileAsString(filename));
            ARBShaderObjects.glCompileShaderARB(shader);

            if (ARBShaderObjects.glGetObjectParameteriARB(shader, ARBShaderObjects.GL_OBJECT_COMPILE_STATUS_ARB) == GL11.GL_FALSE) {
                throw new RuntimeException("Error creating shader: " + getLogInfo(shader));
            }

            return shader;
        } catch (Exception e) {
            ARBShaderObjects.glDeleteObjectARB(shader);
            throw e;
        }
    }

    private static String getLogInfo(int obj) {
        return ARBShaderObjects.glGetInfoLogARB(obj, ARBShaderObjects.glGetObjectParameteriARB(obj, ARBShaderObjects.GL_OBJECT_INFO_LOG_LENGTH_ARB));
    }
}

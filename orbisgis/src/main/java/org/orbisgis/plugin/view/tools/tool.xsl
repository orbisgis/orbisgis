<xsl:stylesheet version = '1.0'
     xmlns:xsl='http://www.w3.org/1999/XSL/Transform'>
<xsl:output method="text" encoding="Cp1252"/>

<xsl:template match="/">
	<xsl:apply-templates/>
</xsl:template>

<xsl:template match="automaton">
package [PACKAGE];

import java.awt.Graphics;
import java.net.URL;
import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.orbisgis.plugin.view.tools.Automaton;
import org.orbisgis.plugin.view.tools.DrawingException;
import org.orbisgis.plugin.view.tools.EditionContext;
import org.orbisgis.plugin.view.tools.FinishedAutomatonException;
import org.orbisgis.plugin.view.tools.NoSuchTransitionException;
import org.orbisgis.plugin.view.tools.ToolManager;
import org.orbisgis.plugin.view.tools.TransitionException;

public abstract class <xsl:value-of select="@name"/> implements Automaton {

	private static Logger logger = Logger.getLogger(<xsl:value-of select="@name"/>.class.getName());

	private String status = "<xsl:value-of select="@initial-status"/>";

	protected EditionContext ec;

	protected ToolManager tm;

	public String[] getTransitionLabels() {
		ArrayList&lt;String&gt; ret = new ArrayList&lt;String&gt;();
		<xsl:for-each select="node">
		if ("<xsl:value-of select="@name"/>".equals(status)) {
			<xsl:for-each select="transition">
				<xsl:if test="@label">
				ret.add(Messages.getString("<xsl:value-of select="@label"/>"));
				</xsl:if>
			</xsl:for-each>
		}
		</xsl:for-each>

		<xsl:for-each select="transition">
			<xsl:if test="@label">
			ret.add(Messages.getString("<xsl:value-of select="@label"/>"));
			</xsl:if>
		</xsl:for-each>

		return ret.toArray(new String[0]);
	}

	public String[] getTransitionCodes() {
		ArrayList&lt;String&gt; ret = new ArrayList&lt;String&gt;();
		<xsl:for-each select="node">
		if ("<xsl:value-of select="@name"/>".equals(status)) {
			<xsl:for-each select="transition">
				<xsl:if test="@label">
				ret.add("<xsl:value-of select="@code"/>");
				</xsl:if>
			</xsl:for-each>
		}
		</xsl:for-each>

		<xsl:for-each select="transition">
			<xsl:if test="@label">
			ret.add("<xsl:value-of select="@code"/>");
			</xsl:if>
		</xsl:for-each>

		return ret.toArray(new String[0]);
	}

	public void init(EditionContext ed, ToolManager tm) throws TransitionException, FinishedAutomatonException {
		logger.info("status: " + status);
		this.ec = ed;
		this.tm = tm;
		status = "<xsl:value-of select="@initial-status"/>";
		transitionTo_<xsl:value-of select="@initial-status"/>();
		if (isFinished(status)){
			throw new FinishedAutomatonException();
		}
	}

	public void transition(String code) throws NoSuchTransitionException, TransitionException, FinishedAutomatonException {
		logger.info("transition code: " + code);

		<xsl:for-each select="node">
		if ("<xsl:value-of select="@name"/>".equals(status)) {
			<xsl:for-each select="transition">
			if ("<xsl:value-of select="@code"/>".equals(code)) {
				String preStatus = status;
				try {
					status = "<xsl:value-of select="@to"/>";
					logger.info("status: " + status);
					double[] v = tm.getValues();
					for (int i = 0; i &lt; v.length; i++) {
						logger.info("value: " + v[i]);
					}
					transitionTo_<xsl:value-of select="@to"/>();
					if (isFinished(status)){
						throw new FinishedAutomatonException();
					}
					return;
				} catch (TransitionException e) {
					status = preStatus;
					throw e;
				}
			}
			</xsl:for-each>
		}
		</xsl:for-each>

		<xsl:for-each select="transition">
		if ("<xsl:value-of select="@code"/>".equals(code)) {
			status = "<xsl:value-of select="@to"/>";
			transitionTo_<xsl:value-of select="@to"/>();
			if (isFinished(status)){
				throw new FinishedAutomatonException();
			}
			return;
		}
		</xsl:for-each>

		throw new NoSuchTransitionException(code);
	}

	public boolean isFinished(String status) {

		<xsl:for-each select="node">
		if ("<xsl:value-of select="@name"/>".equals(status)) {
			<xsl:if test="transition">
				return false;
			</xsl:if>
			<xsl:if test="not(transition)">
				return true;
			</xsl:if>
		}
		</xsl:for-each>

		throw new RuntimeException("Invalid status: " + status);
	}


	public void draw(Graphics g) throws DrawingException {
		<xsl:for-each select="node">
		if ("<xsl:value-of select="@name"/>".equals(status)) {
			drawIn_<xsl:value-of select="@name"/>(g);
		}
		</xsl:for-each>
	}

	<xsl:for-each select="node">
	public abstract void transitionTo_<xsl:value-of select="@name"/>() throws FinishedAutomatonException, TransitionException;
	public abstract void drawIn_<xsl:value-of select="@name"/>(Graphics g) throws DrawingException;
	</xsl:for-each>

	protected void setStatus(String status) throws NoSuchTransitionException {
		this.status = status;
	}

	public String getStatus() {
		return status;
	}

	public String getName() {
		return "<xsl:value-of select="@name"/>";
	}

	public String getMessage() {
		<xsl:for-each select="node">
		if ("<xsl:value-of select="@name"/>".equals(status)) {
			return Messages.getString("<xsl:value-of select="@text"/>");
		}
		</xsl:for-each>

		throw new RuntimeException();
	}

	public String getConsoleCommand() {
		return "<xsl:value-of select="@command"/>";
	}

	public String getTooltip() {
		return Messages.getString("<xsl:value-of select="@tooltip"/>");
	}

	public URL getIconURL() {
		return this.getClass().getResource("<xsl:value-of select="concat('/', translate(//@package,'.','/'), '/', @icon)"/>");
	}

	public URL getMouseCursorURL() {
		<xsl:if test="@cursor">
		return this.getClass().getResource("<xsl:value-of select="concat('/', translate(//@package,'.','/'), '/', @cursor)"/>");
		</xsl:if>
		<xsl:if test="not(@cursor)">
		return null;
		</xsl:if>
	}

	public void toolFinished() throws NoSuchTransitionException, TransitionException, FinishedAutomatonException {
		<xsl:for-each select="node">
		if ("<xsl:value-of select="@name"/>".equals(status)) {
			<xsl:for-each select="transition">
				<xsl:if test="@on-exit">
			transition("<xsl:value-of select="@code"/>");
				</xsl:if>
			</xsl:for-each>
		}
		</xsl:for-each>
	}

}
</xsl:template>

</xsl:stylesheet>
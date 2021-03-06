using System;
using UnityEngine.Events;
using UnityEngine.EventSystems;
using UnityEngine;
using UnityEngine.UI;


/**
 * 
 * 添加center to功能（仅用于page翻页，不实用于小item的居中（待扩展））
 * 不支持scroll bar
 * 
 * 2016-10-13
 * 
 */

[ExecuteInEditMode]
[DisallowMultipleComponent]
[RequireComponent(typeof(RectTransform))]
public class ULScrollRect : UIBehaviour, IInitializePotentialDragHandler, IBeginDragHandler, IEndDragHandler, IDragHandler, IScrollHandler, ICanvasElement, ILayoutElement, ILayoutGroup
{
	public enum MovementType
	{
		Unrestricted, // Unrestricted movement -- can scroll forever
		Elastic, // Restricted but flexible -- can go past the edges, but springs back in place
		Clamped, // Restricted movement where it's not possible to go past the edges
	}

	public enum ScrollbarVisibility
	{
		Permanent,
		AutoHide,
		AutoHideAndExpandViewport,
	}

	[SerializeField]
	private RectTransform m_Content;
	public RectTransform content { get { return m_Content; } set { m_Content = value; } }

	[SerializeField]
	private bool m_Horizontal = false;
	public bool horizontal { get { return m_Horizontal; } set { m_Horizontal = value; } }

	[SerializeField]
	private bool m_Vertical = true;
	public bool vertical { get { return m_Vertical; } set { m_Vertical = value; } }

//	[SerializeField]
	private MovementType m_MovementType = MovementType.Elastic;
	// 暂不开放
	/*public*/ MovementType movementType { get { return m_MovementType; } set { m_MovementType = value; } }

	[SerializeField]
	private float m_Elasticity = 0.1f; // Only used for MovementType.Elastic
	public float elasticity { get { return m_Elasticity; } set { m_Elasticity = value; } }

	[SerializeField]
	private bool m_Inertia = true;
	public bool inertia { get { return m_Inertia; } set { m_Inertia = value; } }

	[SerializeField]
	private float m_DecelerationRate = 0.135f; // Only used when inertia is enabled
	public float decelerationRate { get { return m_DecelerationRate; } set { m_DecelerationRate = value; } }

	[SerializeField]
	private float m_ScrollSensitivity = 1.0f;
	public float scrollSensitivity { get { return m_ScrollSensitivity; } set { m_ScrollSensitivity = value; } }

	[SerializeField]
	private RectTransform m_Viewport;
	public RectTransform viewport { get { return m_Viewport; } set { m_Viewport = value; SetDirtyCaching(); } }

	private Scrollbar m_HorizontalScrollbar;
//	public Scrollbar horizontalScrollbar
//	{
//		get
//		{
//			return m_HorizontalScrollbar;
//		}
//		set
//		{
//			if (m_HorizontalScrollbar)
//				m_HorizontalScrollbar.onValueChanged.RemoveListener(SetHorizontalNormalizedPosition);
//			m_HorizontalScrollbar = value;
//			if (m_HorizontalScrollbar)
//				m_HorizontalScrollbar.onValueChanged.AddListener(SetHorizontalNormalizedPosition);
//			SetDirtyCaching();
//		}
//	}

	private Scrollbar m_VerticalScrollbar;
//	public Scrollbar verticalScrollbar
//	{
//		get
//		{
//			return m_VerticalScrollbar;
//		}
//		set
//		{
//			if (m_VerticalScrollbar)
//				m_VerticalScrollbar.onValueChanged.RemoveListener(SetVerticalNormalizedPosition);
//			m_VerticalScrollbar = value;
//			if (m_VerticalScrollbar)
//				m_VerticalScrollbar.onValueChanged.AddListener(SetVerticalNormalizedPosition);
//			SetDirtyCaching();
//		}
//	}

	private ScrollbarVisibility m_HorizontalScrollbarVisibility;
//	public ScrollbarVisibility horizontalScrollbarVisibility { get { return m_HorizontalScrollbarVisibility; } set { m_HorizontalScrollbarVisibility = value; SetDirtyCaching(); } }

	private ScrollbarVisibility m_VerticalScrollbarVisibility;
//	public ScrollbarVisibility verticalScrollbarVisibility { get { return m_VerticalScrollbarVisibility; } set { m_VerticalScrollbarVisibility = value; SetDirtyCaching(); } }

	private float m_HorizontalScrollbarSpacing;
//	public float horizontalScrollbarSpacing { get { return m_HorizontalScrollbarSpacing; } set { m_HorizontalScrollbarSpacing = value; SetDirty(); } }

	private float m_VerticalScrollbarSpacing;
//	public float verticalScrollbarSpacing { get { return m_VerticalScrollbarSpacing; } set { m_VerticalScrollbarSpacing = value; SetDirty(); } }

//	[Serialize`Field]
	private ScrollRect.ScrollRectEvent m_OnValueChanged = new ScrollRect.ScrollRectEvent();
	/*public*/ ScrollRect.ScrollRectEvent onValueChanged { get { return m_OnValueChanged; } set { m_OnValueChanged = value; } }

	// The offset from handle position to mouse down position
	private Vector2 m_PointerStartLocalCursor = Vector2.zero;
	private Vector2 m_ContentStartPosition = Vector2.zero;

	private RectTransform m_ViewRect;

	protected RectTransform viewRect
	{
		get
		{
			if (m_ViewRect == null)
				m_ViewRect = m_Viewport;
			if (m_ViewRect == null)
				m_ViewRect = (RectTransform)transform;
			return m_ViewRect;
		}
	}

	private Bounds m_ContentBounds;
	private Bounds m_ViewBounds;

	private Vector2 m_Velocity;
	public Vector2 velocity { get { return m_Velocity; } set { m_Velocity = value; } }

	private bool m_Dragging;

	private Vector2 m_PrevPosition = Vector2.zero;
	private Bounds m_PrevContentBounds;
	private Bounds m_PrevViewBounds;
	[NonSerialized]
	private bool m_HasRebuiltLayout = false;

	private bool m_HSliderExpand;
	private bool m_VSliderExpand;
	private float m_HSliderHeight;
	private float m_VSliderWidth;

	[System.NonSerialized] private RectTransform m_Rect;
	private RectTransform rectTransform
	{
		get
		{
			if (m_Rect == null)
				m_Rect = GetComponent<RectTransform>();
			return m_Rect;
		}
	}

	private RectTransform m_HorizontalScrollbarRect;
	private RectTransform m_VerticalScrollbarRect;

	private DrivenRectTransformTracker m_Tracker;

	protected ULScrollRect()
	{
		flexibleWidth = -1;
	}

	public virtual void Rebuild(CanvasUpdate executing)
	{
		if (executing == CanvasUpdate.Prelayout)
		{
			UpdateCachedData();
		}

		if (executing == CanvasUpdate.PostLayout)
		{
			UpdateBounds();
			UpdateScrollbars(Vector2.zero);
			UpdatePrevData();

			m_HasRebuiltLayout = true;
		}
	}

	public virtual void LayoutComplete()
	{}

	public virtual void GraphicUpdateComplete()
	{}

	void UpdateCachedData()
	{
		m_HorizontalScrollbarRect = m_HorizontalScrollbar == null ? null : m_HorizontalScrollbar.transform as RectTransform;
		m_VerticalScrollbarRect = m_VerticalScrollbar == null ? null : m_VerticalScrollbar.transform as RectTransform;

		m_HSliderExpand = false;
		m_VSliderExpand = false;
		m_HSliderHeight = 0;
		m_VSliderWidth = 0;
	}

	protected override void OnEnable()
	{
		base.OnEnable();

		if (m_HorizontalScrollbar)
			m_HorizontalScrollbar.onValueChanged.AddListener(SetHorizontalNormalizedPosition);
		if (m_VerticalScrollbar)
			m_VerticalScrollbar.onValueChanged.AddListener(SetVerticalNormalizedPosition);

		CanvasUpdateRegistry.RegisterCanvasElementForLayoutRebuild(this);
	}

	protected override void OnDisable()
	{
		CanvasUpdateRegistry.UnRegisterCanvasElementForRebuild(this);

		if (m_HorizontalScrollbar)
			m_HorizontalScrollbar.onValueChanged.RemoveListener(SetHorizontalNormalizedPosition);
		if (m_VerticalScrollbar)
			m_VerticalScrollbar.onValueChanged.RemoveListener(SetVerticalNormalizedPosition);

		m_HasRebuiltLayout = false;
		m_Tracker.Clear();
		m_Velocity = Vector2.zero;
		LayoutRebuilder.MarkLayoutForRebuild(rectTransform);
		base.OnDisable();
	}

	public override bool IsActive()
	{
		return base.IsActive() && m_Content != null;
	}

	private void EnsureLayoutHasRebuilt()
	{
		if (!m_HasRebuiltLayout && !CanvasUpdateRegistry.IsRebuildingLayout())
			Canvas.ForceUpdateCanvases();
	}

	public virtual void StopMovement()
	{
		m_Velocity = Vector2.zero;
	}

	public virtual void OnScroll(PointerEventData data)
	{
		if (!IsActive())
			return;

		EnsureLayoutHasRebuilt();
		UpdateBounds();

		Vector2 delta = data.scrollDelta;
		// Down is positive for scroll events, while in UI system up is positive.
		delta.y *= -1;
		if (vertical && !horizontal)
		{
			if (Mathf.Abs(delta.x) > Mathf.Abs(delta.y))
				delta.y = delta.x;
			delta.x = 0;
		}
		if (horizontal && !vertical)
		{
			if (Mathf.Abs(delta.y) > Mathf.Abs(delta.x))
				delta.x = delta.y;
			delta.y = 0;
		}

		Vector2 position = m_Content.anchoredPosition;
		position += delta * m_ScrollSensitivity;
		if (m_MovementType == MovementType.Clamped)
			position += CalculateOffset(position - m_Content.anchoredPosition);

		SetContentAnchoredPosition(position);
		UpdateBounds();
	}

	public virtual void OnInitializePotentialDrag(PointerEventData eventData)
	{
		if (eventData.button != PointerEventData.InputButton.Left)
			return;

		m_Velocity = Vector2.zero;
	}

	public virtual void OnBeginDrag(PointerEventData eventData)
	{
		if (eventData.button != PointerEventData.InputButton.Left)
			return;

		if (!IsActive())
			return;

		UpdateBounds();

		m_PointerStartLocalCursor = Vector2.zero;
		RectTransformUtility.ScreenPointToLocalPointInRectangle(viewRect, eventData.position, eventData.pressEventCamera, out m_PointerStartLocalCursor);
		m_ContentStartPosition = m_Content.anchoredPosition;
		m_Dragging = true;
	}

	public virtual void OnEndDrag(PointerEventData eventData)
	{
		if (eventData.button != PointerEventData.InputButton.Left)
			return;

		m_Dragging = false;
	}

	public virtual void OnDrag(PointerEventData eventData)
	{
		if (eventData.button != PointerEventData.InputButton.Left)
			return;

		if (!IsActive())
			return;

		Vector2 localCursor;
		if (!RectTransformUtility.ScreenPointToLocalPointInRectangle(viewRect, eventData.position, eventData.pressEventCamera, out localCursor))
			return;

		UpdateBounds();

		var pointerDelta = localCursor - m_PointerStartLocalCursor;
		Vector2 position = m_ContentStartPosition + pointerDelta;

		// Offset to get content into place in the view.
		Vector2 offset = CalculateOffset(position - m_Content.anchoredPosition);
		position += offset;
		if (m_MovementType == MovementType.Elastic)
		{
			if (offset.x != 0)
				position.x = position.x - RubberDelta(offset.x, m_ViewBounds.size.x);
			if (offset.y != 0)
				position.y = position.y - RubberDelta(offset.y, m_ViewBounds.size.y);
		}

		SetContentAnchoredPosition(position);
	}

	protected virtual void SetContentAnchoredPosition(Vector2 position)
	{
		if (!m_Horizontal)
			position.x = m_Content.anchoredPosition.x;
		if (!m_Vertical)
			position.y = m_Content.anchoredPosition.y;

		if (position != m_Content.anchoredPosition)
		{
			m_Content.anchoredPosition = position;
			UpdateBounds();
		}
	}

	protected virtual void LateUpdate()
	{
		if (!m_Content)
			return;

		EnsureLayoutHasRebuilt();
		UpdateScrollbarVisibility();
		UpdateBounds();
		float deltaTime = Time.unscaledDeltaTime;
		Vector2 offset = CalculateOffset(Vector2.zero);
		if (!m_Dragging && (offset != Vector2.zero || m_Velocity != Vector2.zero))
		{
			Vector2 position = m_Content.anchoredPosition;
			for (int axis = 0; axis < 2; axis++)
			{
				// Apply spring physics if movement is elastic and content has an offset from the view.
				if (m_MovementType == MovementType.Elastic && offset[axis] != 0)
				{
					float speed = m_Velocity[axis];
					position[axis] = Mathf.SmoothDamp(m_Content.anchoredPosition[axis], m_Content.anchoredPosition[axis] + offset[axis], ref speed, m_Elasticity, Mathf.Infinity, deltaTime);
					m_Velocity[axis] = speed;
				}
				// Else move content according to velocity with deceleration applied.
				else if (m_Inertia)
				{
					m_Velocity[axis] *= Mathf.Pow(m_DecelerationRate, deltaTime);
					if (Mathf.Abs(m_Velocity[axis]) < 1)
						m_Velocity[axis] = 0;
					position[axis] += m_Velocity[axis] * deltaTime;
				}
				// If we have neither elaticity or friction, there shouldn't be any velocity.
				else
				{
					m_Velocity[axis] = 0;
				}
			}

			if (m_Velocity != Vector2.zero)
			{
				if (m_MovementType == MovementType.Clamped)
				{
					offset = CalculateOffset(position - m_Content.anchoredPosition);
					position += offset;
				}

				// 强制修正，避免offset和velocity在小浮点数一直波动，小于0.5的波动几乎被肉眼识别
				if(centerOnChild && offset.magnitude < 0.5f && m_Velocity.magnitude < 0.5){
					offset = m_Velocity = Vector2.zero;
					int index = getIndexByLocalPosition(centerX, centerY);
					Vector3 pos = getLocalPositionByIndex(index);
					Vector2 modify = new Vector2(centerX - pos.x, centerY - pos.y);
					position = m_Content.anchoredPosition + modify;
				}

				SetContentAnchoredPosition(position);
			}
		}

		if (m_Dragging && m_Inertia)
		{
			Vector3 newVelocity = (m_Content.anchoredPosition - m_PrevPosition) / deltaTime;
			m_Velocity = Vector3.Lerp(m_Velocity, newVelocity, deltaTime * 10);
		}

		if (m_ViewBounds != m_PrevViewBounds || m_ContentBounds != m_PrevContentBounds || m_Content.anchoredPosition != m_PrevPosition)
		{
			UpdateScrollbars(offset);
			m_OnValueChanged.Invoke(normalizedPosition);
			UpdatePrevData();
		}
	}

	private void UpdatePrevData()
	{
		if (m_Content == null)
			m_PrevPosition = Vector2.zero;
		else
			m_PrevPosition = m_Content.anchoredPosition;
		m_PrevViewBounds = m_ViewBounds;
		m_PrevContentBounds = m_ContentBounds;
	}

	private void UpdateScrollbars(Vector2 offset)
	{
		if (m_HorizontalScrollbar)
		{
			if (m_ContentBounds.size.x > 0)
				m_HorizontalScrollbar.size = Mathf.Clamp01((m_ViewBounds.size.x - Mathf.Abs(offset.x)) / m_ContentBounds.size.x);
			else
				m_HorizontalScrollbar.size = 1;

			m_HorizontalScrollbar.value = horizontalNormalizedPosition;
		}

		if (m_VerticalScrollbar)
		{
			if (m_ContentBounds.size.y > 0)
				m_VerticalScrollbar.size = Mathf.Clamp01((m_ViewBounds.size.y - Mathf.Abs(offset.y)) / m_ContentBounds.size.y);
			else
				m_VerticalScrollbar.size = 1;

			m_VerticalScrollbar.value = verticalNormalizedPosition;
		}
	}

	public Vector2 normalizedPosition
	{
		get
		{
			return new Vector2(horizontalNormalizedPosition, verticalNormalizedPosition);
		}
		set
		{
			SetNormalizedPosition(value.x, 0);
			SetNormalizedPosition(value.y, 1);
		}
	}


	public float horizontalNormalizedPosition
	{
		get
		{
			UpdateBounds();
			if (m_ContentBounds.size.x <= m_ViewBounds.size.x)
				return (m_ViewBounds.min.x > m_ContentBounds.min.x) ? 1 : 0;
			return (m_ViewBounds.min.x - m_ContentBounds.min.x) / (m_ContentBounds.size.x - m_ViewBounds.size.x);
		}
		set
		{
			SetNormalizedPosition(value, 0);
		}
	}

	public float verticalNormalizedPosition
	{
		get
		{
			UpdateBounds();
			if (m_ContentBounds.size.y <= m_ViewBounds.size.y)
				return (m_ViewBounds.min.y > m_ContentBounds.min.y) ? 1 : 0;
			;
			return (m_ViewBounds.min.y - m_ContentBounds.min.y) / (m_ContentBounds.size.y - m_ViewBounds.size.y);
		}
		set
		{
			SetNormalizedPosition(value, 1);
		}
	}

	private void SetHorizontalNormalizedPosition(float value) { SetNormalizedPosition(value, 0); }
	private void SetVerticalNormalizedPosition(float value) { SetNormalizedPosition(value, 1); }

	private void SetNormalizedPosition(float value, int axis)
	{
		EnsureLayoutHasRebuilt();
		UpdateBounds();
		// How much the content is larger than the view.
		float hiddenLength = m_ContentBounds.size[axis] - m_ViewBounds.size[axis];
		// Where the position of the lower left corner of the content bounds should be, in the space of the view.
		float contentBoundsMinPosition = m_ViewBounds.min[axis] - value * hiddenLength;
		// The new content localPosition, in the space of the view.
		float newLocalPosition = m_Content.localPosition[axis] + contentBoundsMinPosition - m_ContentBounds.min[axis];

		Vector3 localPosition = m_Content.localPosition;
		if (Mathf.Abs(localPosition[axis] - newLocalPosition) > 0.01f)
		{
			localPosition[axis] = newLocalPosition;
			m_Content.localPosition = localPosition;
			m_Velocity[axis] = 0;
			UpdateBounds();
		}
	}

	private static float RubberDelta(float overStretching, float viewSize)
	{
		return (1 - (1 / ((Mathf.Abs(overStretching) * 0.55f / viewSize) + 1))) * viewSize * Mathf.Sign(overStretching);
	}

	protected override void OnRectTransformDimensionsChange()
	{
		SetDirty();
	}

	private bool hScrollingNeeded
	{
		get
		{
			if (Application.isPlaying)
				return m_ContentBounds.size.x > m_ViewBounds.size.x + 0.01f;
			return true;
		}
	}
	private bool vScrollingNeeded
	{
		get
		{
			if (Application.isPlaying)
				return m_ContentBounds.size.y > m_ViewBounds.size.y + 0.01f;
			return true;
		}
	}

	public virtual void CalculateLayoutInputHorizontal() {}
	public virtual void CalculateLayoutInputVertical() {}

	public virtual float minWidth { get { return -1; } }
	public virtual float preferredWidth { get { return -1; } }
	public virtual float flexibleWidth { get; private set; }

	public virtual float minHeight { get { return -1; } }
	public virtual float preferredHeight { get { return -1; } }
	public virtual float flexibleHeight { get { return -1; } }

	public virtual int layoutPriority { get { return -1; } }

	public virtual void SetLayoutHorizontal()
	{
		m_Tracker.Clear();

		if (m_HSliderExpand || m_VSliderExpand)
		{
			m_Tracker.Add(this, viewRect,
				DrivenTransformProperties.Anchors |
				DrivenTransformProperties.SizeDelta |
				DrivenTransformProperties.AnchoredPosition);

			// Make view full size to see if content fits.
			viewRect.anchorMin = Vector2.zero;
			viewRect.anchorMax = Vector2.one;
			viewRect.sizeDelta = Vector2.zero;
			viewRect.anchoredPosition = Vector2.zero;

			// Recalculate content layout with this size to see if it fits when there are no scrollbars.
			LayoutRebuilder.ForceRebuildLayoutImmediate(content);
			m_ViewBounds = new Bounds(viewRect.rect.center, viewRect.rect.size);
			m_ContentBounds = GetBounds();
		}

		// If it doesn't fit vertically, enable vertical scrollbar and shrink view horizontally to make room for it.
		if (m_VSliderExpand && vScrollingNeeded)
		{
			viewRect.sizeDelta = new Vector2(-(m_VSliderWidth + m_VerticalScrollbarSpacing), viewRect.sizeDelta.y);

			// Recalculate content layout with this size to see if it fits vertically
			// when there is a vertical scrollbar (which may reflowed the content to make it taller).
			LayoutRebuilder.ForceRebuildLayoutImmediate(content);
			m_ViewBounds = new Bounds(viewRect.rect.center, viewRect.rect.size);
			m_ContentBounds = GetBounds();
		}

		// If it doesn't fit horizontally, enable horizontal scrollbar and shrink view vertically to make room for it.
		if (m_HSliderExpand && hScrollingNeeded)
		{
			viewRect.sizeDelta = new Vector2(viewRect.sizeDelta.x, -(m_HSliderHeight + m_HorizontalScrollbarSpacing));
			m_ViewBounds = new Bounds(viewRect.rect.center, viewRect.rect.size);
			m_ContentBounds = GetBounds();
		}

		// If the vertical slider didn't kick in the first time, and the horizontal one did,
		// we need to check again if the vertical slider now needs to kick in.
		// If it doesn't fit vertically, enable vertical scrollbar and shrink view horizontally to make room for it.
		if (m_VSliderExpand && vScrollingNeeded && viewRect.sizeDelta.x == 0 && viewRect.sizeDelta.y < 0)
		{
			viewRect.sizeDelta = new Vector2(-(m_VSliderWidth + m_VerticalScrollbarSpacing), viewRect.sizeDelta.y);
		}
	}

	public virtual void SetLayoutVertical()
	{
		UpdateScrollbarLayout();
		m_ViewBounds = new Bounds(viewRect.rect.center, viewRect.rect.size);
		m_ContentBounds = GetBounds();
	}

	void UpdateScrollbarVisibility()
	{
		if (m_VerticalScrollbar && m_VerticalScrollbarVisibility != ScrollbarVisibility.Permanent && m_VerticalScrollbar.gameObject.activeSelf != vScrollingNeeded)
			m_VerticalScrollbar.gameObject.SetActive(vScrollingNeeded);

		if (m_HorizontalScrollbar && m_HorizontalScrollbarVisibility != ScrollbarVisibility.Permanent && m_HorizontalScrollbar.gameObject.activeSelf != hScrollingNeeded)
			m_HorizontalScrollbar.gameObject.SetActive(hScrollingNeeded);
	}

	void UpdateScrollbarLayout()
	{
		if (m_VSliderExpand && m_HorizontalScrollbar)
		{
			m_Tracker.Add(this, m_HorizontalScrollbarRect,
				DrivenTransformProperties.AnchorMinX |
				DrivenTransformProperties.AnchorMaxX |
				DrivenTransformProperties.SizeDeltaX |
				DrivenTransformProperties.AnchoredPositionX);
			m_HorizontalScrollbarRect.anchorMin = new Vector2(0, m_HorizontalScrollbarRect.anchorMin.y);
			m_HorizontalScrollbarRect.anchorMax = new Vector2(1, m_HorizontalScrollbarRect.anchorMax.y);
			m_HorizontalScrollbarRect.anchoredPosition = new Vector2(0, m_HorizontalScrollbarRect.anchoredPosition.y);
			if (vScrollingNeeded)
				m_HorizontalScrollbarRect.sizeDelta = new Vector2(-(m_VSliderWidth + m_VerticalScrollbarSpacing), m_HorizontalScrollbarRect.sizeDelta.y);
			else
				m_HorizontalScrollbarRect.sizeDelta = new Vector2(0, m_HorizontalScrollbarRect.sizeDelta.y);
		}

		if (m_HSliderExpand && m_VerticalScrollbar)
		{
			m_Tracker.Add(this, m_VerticalScrollbarRect,
				DrivenTransformProperties.AnchorMinY |
				DrivenTransformProperties.AnchorMaxY |
				DrivenTransformProperties.SizeDeltaY |
				DrivenTransformProperties.AnchoredPositionY);
			m_VerticalScrollbarRect.anchorMin = new Vector2(m_VerticalScrollbarRect.anchorMin.x, 0);
			m_VerticalScrollbarRect.anchorMax = new Vector2(m_VerticalScrollbarRect.anchorMax.x, 1);
			m_VerticalScrollbarRect.anchoredPosition = new Vector2(m_VerticalScrollbarRect.anchoredPosition.x, 0);
			if (hScrollingNeeded)
				m_VerticalScrollbarRect.sizeDelta = new Vector2(m_VerticalScrollbarRect.sizeDelta.x, -(m_HSliderHeight + m_HorizontalScrollbarSpacing));
			else
				m_VerticalScrollbarRect.sizeDelta = new Vector2(m_VerticalScrollbarRect.sizeDelta.x, 0);
		}
	}

	private void UpdateBounds()
	{
		m_ViewBounds = new Bounds(viewRect.rect.center, viewRect.rect.size);
		m_ContentBounds = GetBounds();

		if (m_Content == null)
			return;

		// Make sure content bounds are at least as large as view by adding padding if not.
		// One might think at first that if the content is smaller than the view, scrolling should be allowed.
		// However, that's not how scroll views normally work.
		// Scrolling is *only* possible when content is *larger* than view.
		// We use the pivot of the content rect to decide in which directions the content bounds should be expanded.
		// E.g. if pivot is at top, bounds are expanded downwards.
		// This also works nicely when ContentSizeFitter is used on the content.
		Vector3 contentSize = m_ContentBounds.size;
		Vector3 contentPos = m_ContentBounds.center;
		Vector3 excess = m_ViewBounds.size - contentSize;
		if (excess.x > 0)
		{
			contentPos.x -= excess.x * (m_Content.pivot.x - 0.5f);
			contentSize.x = m_ViewBounds.size.x;
		}
		if (excess.y > 0)
		{
			contentPos.y -= excess.y * (m_Content.pivot.y - 0.5f);
			contentSize.y = m_ViewBounds.size.y;
		}

		m_ContentBounds.size = contentSize;
		m_ContentBounds.center = contentPos;
	}

	private readonly Vector3[] m_Corners = new Vector3[4];
	private Bounds GetBounds()
	{
		if (m_Content == null)
			return new Bounds();

		var vMin = new Vector3(float.MaxValue, float.MaxValue, float.MaxValue);
		var vMax = new Vector3(float.MinValue, float.MinValue, float.MinValue);

		var toLocal = viewRect.worldToLocalMatrix;
		m_Content.GetWorldCorners(m_Corners);
		for (int j = 0; j < 4; j++)
		{
			Vector3 v = toLocal.MultiplyPoint3x4(m_Corners[j]);
			vMin = Vector3.Min(v, vMin);
			vMax = Vector3.Max(v, vMax);
		}

		var bounds = new Bounds(vMin, Vector3.zero);
		bounds.Encapsulate(vMax);
		return bounds;
	}


	private Vector2 m_destPos = Vector2.zero;

	private float centerX {
		get{
			if(m_Horizontal){
				return viewRect.rect.width / 2 - content.anchoredPosition.x;
			} else {
				return viewRect.rect.width / 2;
			}
		}
	}

	private float centerY {
		get{
			if(m_Vertical){
				return - viewRect.rect.height / 2 - content.anchoredPosition.y;
			} else {
				return - viewRect.rect.height / 2;
			}
		}
	}


	private Vector2 CalculateOffset(Vector2 delta)
	{
		Vector2 offset = Vector2.zero;
		if (m_MovementType == MovementType.Unrestricted)
			return offset;

		Vector2 min = m_ContentBounds.min;
		Vector2 max = m_ContentBounds.max;

		if (m_Horizontal)
		{
			min.x += delta.x;
			max.x += delta.x;
			if (min.x > m_ViewBounds.min.x)
				offset.x = m_ViewBounds.min.x - min.x;
			else if (max.x < m_ViewBounds.max.x)
				offset.x = m_ViewBounds.max.x - max.x;
		}

		if (m_Vertical)
		{
			min.y += delta.y;
			max.y += delta.y;
			if (max.y < m_ViewBounds.max.y)
				offset.y = m_ViewBounds.max.y - max.y;
			else if (min.y > m_ViewBounds.min.y)
				offset.y = m_ViewBounds.min.y - min.y;
		}

		// CenterToChild
//		var magnitude = m_Velocity.magnitude;
		if( /*magnitude < 300 &&*/ ( !m_Dragging && offset == Vector2.zero) ){
			if(m_destPos == Vector2.zero){
				CenterToChild(ref offset);
			} else {
				offset.x = centerX - m_destPos.x;
				offset.y = centerY - m_destPos.y;

				if(offset.magnitude < 0.1)
					m_destPos = Vector2.zero;
			}
		}

		return offset;
	}


	private void CenterToChild(ref Vector2 offset){
		if(!centerOnChild)
			return;
		curTopIndex = curTopIndex;
		int index = getIndexByLocalPosition(centerX, centerY);
		Vector3 pos = getLocalPositionByIndex(index);

		if(m_Vertical){
			offset.y = centerY - pos.y;
		} else {
			offset.x = centerX - pos.x;
		}

//		var childCount = content.childCount;
//		var r = itemRect;
//		RectTransform child;
//		for(int i = 0; i < childCount; i++){
//			child = (RectTransform)content.GetChild(i);
//			var ap = child.anchoredPosition;
//			if(m_Vertical){
//				if(ap.y + r.height / 2 > centerY && ap.y - r.height / 2 < centerY){
//					offset.y = centerY - ap.y;
//					break;
//				}
//			} else if (m_Horizontal){
//				if(ap.x + r.width / 2 > centerX && ap.x - r.width/ 2 < centerX){
//					offset.x = centerX - ap.x;
//					break;
//				}
//			}
//		}
	}

	public void Next(){
		int index = getIndexByLocalPosition(centerX, centerY);
		index = index >= totalCount - 1? totalCount - 1 : index + 1;
		m_destPos = getLocalPositionByIndex(index);
	}


	public void Previous(){
		int index = getIndexByLocalPosition(centerX, centerY);
		index = index <= 0 ? 0 :index - 1;
		m_destPos = getLocalPositionByIndex(index);
	}


	protected void SetDirty()
	{
		if (!IsActive())
			return;

		LayoutRebuilder.MarkLayoutForRebuild(rectTransform);
	}

	protected void SetDirtyCaching()
	{
		if (!IsActive())
			return;

		CanvasUpdateRegistry.RegisterCanvasElementForLayoutRebuild(this);
		LayoutRebuilder.MarkLayoutForRebuild(rectTransform);
	}

	#if UNITY_EDITOR
	protected override void OnValidate()
	{
		SetDirtyCaching();
	}

	#endif


	//------------------------- 复用item --------------------------------------
	GameObject CreateItem(){
		return new GameObject();
	}

	public Action<GameObject> createCallback;

	public Action<GameObject, int> changeCallback;
	
	public GameObject itemTemplate;

	private Rect itemRect;

	public Vector2 itemSpace = Vector2.zero;

	// 总数
	public int totalCount = 1;

	// 列数或行数
	public int itemNumber = 1;

	public bool centerOnChild = true;

	private int itemCount;

	public Vector2 padding = Vector2.zero;
	private GameObject[] itemArray;
	private int curTopIndex = 0;
	private bool isInit = false;

	private void init(){
		// 如果在预设上，则自动隐藏
		if(itemTemplate.transform.parent)
			itemTemplate.SetActive(false);
		
		itemRect = ((RectTransform) itemTemplate.transform).rect;
		onValueChanged.AddListener(handleScrollRectValueChange);
		viewport.pivot = new Vector2(0, 1);
		content.pivot = new Vector2(0, 1);
		int itemCount = 0;
		if(m_Horizontal){
			itemCount = Mathf.CeilToInt(viewport.rect.width / (itemRect.width + itemSpace.x)) + 1;
//			if(centerOnChild)
			padding.y = (viewport.rect.height - itemRect.height * itemNumber - itemSpace.y * (itemNumber - 1) ) * 0.5f;
		} else {
			itemCount = Mathf.CeilToInt(viewport.rect.height / (itemRect.height + itemSpace.y)) + 1;
//			if(centerOnChild)
			padding.x = (viewport.rect.width - itemRect.width * itemNumber - itemSpace.x * (itemNumber - 1)) * 0.5f;
		}

		this.itemCount = itemCount * itemNumber;

		itemArray = new GameObject[this.itemCount];

		for(int i = 1; i <= this.itemCount; i++){
			var obj = GameObject.Instantiate(itemTemplate);
			if(createCallback != null)
				createCallback(obj);

			obj.transform.SetParent(content, false);
			obj.transform.localPosition = getLocalPositionByIndex(i - 1);
			obj.SetActive(true);
			itemArray[i - 1] = obj;
		}

		curTopIndex = 0;
		isInit = true;
	}


	Vector3 getLocalPositionByIndex(int index){
		int aIndex = index % itemNumber;
		int bIndex = Mathf.FloorToInt(index / (float)itemNumber);
		if(m_Vertical){
			int _t = bIndex;
			bIndex = aIndex;
			aIndex = _t;
		}
		return new Vector3(padding.x + itemRect.width * (0.5f + bIndex) + itemSpace.x * bIndex, -padding.y - itemRect.height * (0.5f + aIndex) - itemSpace.y * aIndex, 0f);
	}


	int getIndexByLocalPosition(float x, float y){
		float aIndex = (x - padding.x - itemRect.width * 0.5f) / (itemRect.width + itemSpace.x);
		float bIndex = (y + padding.y + itemRect.height * 0.5f) / (-itemRect.height - itemSpace.y);
		if(m_Vertical){
			float _t = bIndex;
			bIndex = aIndex;
			aIndex = _t;
		}

		int b = (int)bIndex;
		int a = (int)(aIndex < 0 ? aIndex - 0.5f : aIndex + 0.5f);
		return b * itemNumber + a;
	}


	void resetContent(){
		if(m_Horizontal){
			content.sizeDelta = new Vector2((itemRect.width + itemSpace.x) * Mathf.Ceil(totalCount / (float)itemNumber) + padding.x * 2, content.sizeDelta.y);
		} else {
			content.sizeDelta = new Vector2(content.sizeDelta.x, (itemRect.height + itemSpace.y) * Mathf.Ceil(totalCount / (float)itemNumber) + padding.y * 2);
		}

	}


	public void RefreshList(){
		if(!isInit){
			init();
		}

		resetContent();

		for(int i = 0; i < itemArray.Length; i++){
			itemArray[i].transform.localPosition = getLocalPositionByIndex(curTopIndex + i);
			if(curTopIndex + i < totalCount){
				notifyItemChange(itemArray[i], curTopIndex + i + 1);
			} else {
				itemArray[i].SetActive(false);
			}
		}
	}


	void notifyItemChange(GameObject obj, int index){
		if(changeCallback != null)
			changeCallback(obj, index);
	}


	void handleScrollRectValueChange (Vector2 v){
		float distance = 0f;
		float itemSize = 0f;
		if(m_Horizontal){
			float curContentX = content.localPosition.x;
			distance = curContentX + topItemCenterPosX();
			itemSize = itemRect.width + itemSpace.x;

			if(distance < -itemSize * 0.5f){	//向左
				headToTail(Mathf.CeilToInt(distance / -itemSize));
			} else if(distance > itemSize * 0.5f){		//向右
				tailToHead(Mathf.CeilToInt(distance / itemSize));
			}
		} else {
			float curContentY = content.localPosition.y;
			distance = curContentY - topItemCenterPosY();
			itemSize = itemRect.height + itemSpace.y;

			if(distance > itemSize * 0.5f){	//向上
				headToTail(Mathf.CeilToInt(distance / itemSize));
			} else if(distance < -itemSize * 0.5f){		//向下
				tailToHead(Mathf.CeilToInt(distance / -itemSize));
			}
		}
	}

	float topItemCenterPosX(){
		int index = Mathf.FloorToInt(curTopIndex / (float)itemNumber);
		return padding.x + (0.5f + index) * itemRect.width + itemSpace.x * index;
	}

	float topItemCenterPosY(){
		int index = Mathf.FloorToInt(curTopIndex / (float)itemNumber);
		return padding.y + (0.5f + index) * itemRect.height + itemSpace.y * index;
	}


	void headToTail(int updateCount){
		for(int i = 0; i < updateCount * itemNumber; i++){
			int dataIndex = curTopIndex + itemCount;
			if(dataIndex < totalCount){
				var topItem = itemArray[curTopIndex % itemCount];
				topItem.transform.localPosition = getLocalPositionByIndex(dataIndex);
				topItem.SetActive(true);
				notifyItemChange(topItem, dataIndex + 1);
				curTopIndex += 1;
			} else if(curTopIndex % itemCount < itemCount){
				var topItem = itemArray[curTopIndex % itemCount];
				topItem.SetActive(false);
				curTopIndex += 1;
			} else {
				return;
			}
		}
	}

	void tailToHead(float updateCount){
		for(int i = 0; i < updateCount * itemNumber; i++){
			int dataIndex = curTopIndex - 1;
			int index = curTopIndex % itemCount + itemCount - 1;
			if(dataIndex >= 0 && dataIndex < totalCount){
				var bottomItem = itemArray[index >= itemCount ? index - itemCount : index];
				bottomItem.transform.localPosition = getLocalPositionByIndex(dataIndex);
				bottomItem.SetActive(true);
				notifyItemChange(bottomItem, dataIndex + 1);
			} else if(dataIndex >= totalCount){
				var bottomItem = itemArray[index >= itemCount ? index - itemCount : index];
				bottomItem.SetActive(false);
			} else {
				return;
			}

			curTopIndex -= 1;
		}
	}
}
